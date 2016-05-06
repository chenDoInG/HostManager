package hostmanager.presenter;

import hostmanager.api.MenuService;
import hostmanager.enums.FileSystem;
import hostmanager.helper.HostHelper;
import hostmanager.model.Host;
import hostmanager.ui.MainForm;
import rx.Observable;
import rx.Subscriber;
import rx.functions.Action1;

import java.sql.SQLException;

public class MainFormPresenter {

    private MainForm form;
    private MenuService menuService;
    private HostHelper helper;

    public MainFormPresenter(MainForm form, MenuService menuService, HostHelper helper) {
        this.form = form;
        this.menuService = menuService;
        this.helper = helper;
    }

    public void deleteHost(Host host) {
        try {
            helper.deleteHost(host);
        } catch (SQLException e) {
            form.showErrorMsg(e.getMessage());
        }
    }

    public void saveHost(Host host) {
        switch (host.getType()) {
            case "common":
                try {
                    helper.saveCommonHost(host);
                } catch (SQLException e) {
                    form.showErrorMsg(e.getMessage());
                }
                break;
            case "online":
                break;
            default:
                try {
                    helper.saveLocalHost(host);
                    form.updateHost("本地方案", host);
                } catch (SQLException e) {
                    form.showErrorMsg(e.getMessage());
                }

        }
    }

    private Host createCommonHost() {
        Host host = new Host("公共配置");
        host.setType("common");
        try {
            helper.initCommonHost(host);
        } catch (SQLException e) {
            form.showErrorMsg(e.getMessage());
        }
        return host;
    }

    private void initCommonHost() {
        try {
            helper.getCommonHost()
                    .switchIfEmpty(Observable.create(new Observable.OnSubscribe<Host>() {
                        @Override
                        public void call(Subscriber<? super Host> subscriber) {
                            subscriber.onNext(createCommonHost());
                            subscriber.onCompleted();
                        }
                    }))
                    .subscribe(new Action1<Host>() {
                        @Override
                        public void call(Host host) {
                            form.updateHost(host);
                        }
                    }, new Action1<Throwable>() {
                        @Override
                        public void call(Throwable throwable) {
                            form.showErrorMsg(throwable.getMessage());
                        }
                    });
        } catch (SQLException e) {
            form.showErrorMsg(e.getMessage());
        }
    }

    private void initLocalHost() {
        try {
            helper.getLocalHosts()
                    .subscribe(new Action1<Host>() {
                        @Override
                        public void call(Host host) {
                            form.updateHost("本地方案", host);
                        }
                    });
        } catch (SQLException e) {
            form.showErrorMsg(e.getMessage());
        }

    }

    public void askForNewData() {
        initCommonHost();
        FileSystem.os().getHost()
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String s) {
                        Host currentHost = new Host("当前系统host");
                        currentHost.setContent(s);
                        form.updateHost(currentHost);
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        form.showErrorMsg(throwable.getMessage());
                    }
                });

        initLocalHost();
//        menuService.getHosts()
//                .subscribeOn(Schedulers.io())
//                .flatMap(new Func1<List<Repos>, Observable<List<String>>>() {
//                    @Override
//                    public Observable<List<String>> call(List<Repos> reposes) {
//                        final List<String> hostNames = new ArrayList<>();
//                        for (Repos r : reposes) {
//                            hostNames.add(r.getName());
//                        }
//                        return Observable.create(new Observable.OnSubscribe<List<String>>() {
//                            @Override
//                            public void call(Subscriber<? super List<String>> subscriber) {
//                                subscriber.onNext(hostNames);
//                                subscriber.onCompleted();
//                            }
//                        });
//                    }
//                })
//                .subscribe(new Action1<List<String>>() {
//                    @Override
//                    public void call(List<String> hostNames) {
//                        form.updateHosts("在线方案", hostNames);
//                    }
//                }, new Action1<Throwable>() {
//                    @Override
//                    public void call(Throwable throwable) {
//                        throwable.printStackTrace();
//                    }
//                });
    }

}
