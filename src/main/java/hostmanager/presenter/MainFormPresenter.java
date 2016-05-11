package hostmanager.presenter;

import hostmanager.api.MenuService;
import hostmanager.enums.FileSystem;
import hostmanager.helper.HostHelper;
import hostmanager.model.Host;
import hostmanager.ui.MainForm;
import rx.Observable;
import rx.Subscriber;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

import java.util.List;

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
        helper.deleteHost(host);
    }

    public void saveHost(Host host) {
        helper.save(host);
        form.updateHost(host);
    }

    public Observable<Host> getOnlineHost() {
        return menuService.getHosts()
                .flatMap(new Func1<List<String>, Observable<String>>() {

                    @Override
                    public Observable<String> call(final List<String> hostNames) {
                        return Observable.create(new Observable.OnSubscribe<String>() {
                            @Override
                            public void call(Subscriber<? super String> subscriber) {
                                for (String name : hostNames) {
                                    subscriber.onNext(name);
                                }
                                subscriber.onCompleted();
                            }
                        });
                    }
                })
                .flatMap(new Func1<String, Observable<Host>>() {
                    @Override
                    public Observable<Host> call(final String hostName) {
                        return Observable.create(new Observable.OnSubscribe<Host>() {
                            @Override
                            public void call(Subscriber<? super Host> subscriber) {
                                final Host online = Host.online(hostName);
                                menuService.getHost(hostName)
                                        .subscribe(new Action1<String>() {
                                            @Override
                                            public void call(String hostContent) {
                                                online.setContent(hostContent);
                                            }
                                        });
                                subscriber.onNext(online);
                                subscriber.onCompleted();
                            }
                        });
                    }
                })
                .filter(new Func1<Host, Boolean>() {
                    @Override
                    public Boolean call(Host host) {
                        return !"# hosts 不存在或没有生成!".equals(host.getContent());
                    }
                });
    }

    public void askForNewData() {
        helper.getCommonHost()
                .switchIfEmpty(Observable.create(new Observable.OnSubscribe<Host>() {
                    @Override
                    public void call(Subscriber<? super Host> subscriber) {
                        helper.save(Host.common());
                        subscriber.onNext(Host.common());
                        subscriber.onCompleted();
                    }
                }))
                .concatWith(FileSystem.os().getHost())
                .concatWith(helper.getLocalHosts())
                .concatWith(getOnlineHost())
                .subscribeOn(Schedulers.io())
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

        getOnlineHost();
    }

}
