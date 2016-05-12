package hostmanager.presenter;

import hostmanager.api.MenuService;
import hostmanager.enums.FileSystem;
import hostmanager.exception.InCorrectPasswordException;
import hostmanager.helper.HostHelper;
import hostmanager.model.Host;
import hostmanager.ui.MainForm;
import hostmanager.ui.Menu;
import hostmanager.ui.Panel2AddHost;
import hostmanager.ui.Tray;
import rx.Observable;
import rx.Subscriber;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class MainFormPresenter {

    private MainForm form;
    private Tray tray;
    private Menu menu;
    private Panel2AddHost panel2AddHost;

    private MenuService menuService;
    private HostHelper helper;

    public MainFormPresenter(MainForm form, MenuService menuService, HostHelper helper) {
        this.form = form;
        this.menuService = menuService;
        this.helper = helper;
    }

    public void injectMenu(Menu menu) {
        this.menu = menu;
    }

    public void injectTray(Tray tray) {
        this.tray = tray;
    }

    public void injectPanel(Panel2AddHost panel2AddHost) {
        this.panel2AddHost = panel2AddHost;
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
                .subscribeOn(Schedulers.newThread())
                .subscribe(updateHostOnSubscriber);
    }

    private Subscriber<Host> updateHostOnSubscriber = new Subscriber<Host>() {
        @Override
        public void onCompleted() {

        }

        @Override
        public void onError(Throwable e) {
            form.showErrorMsg(e.getMessage());
        }

        @Override
        public void onNext(Host host) {
            menu.updateHost(host);
            tray.updateTray(host.getName());
        }
    };

    private Observable<Host> getOnlineHost() {
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

    public void deleteHost() {
        Host selectedHost = menu.getHostOnShow();
        if (selectedHost != null) {
            if (notOnlineHost(selectedHost)) {
                menu.removeHost();
                tray.deleteTray(selectedHost.getName());
                helper.deleteHost(selectedHost);
            } else {
                showErrorMsg("暂不提供远程host删除功能");
            }

        } else {
            showErrorMsg("您还没有选择任何host");
        }
    }

    public void activeHost() {
        FileSystem.os().getHostFile()
                .subscribeOn(Schedulers.io())
                .subscribe(changeHostOnSubscriber);
    }

    private Subscriber<File> changeHostOnSubscriber = new Subscriber<File>() {
        @Override
        public void onCompleted() {

        }

        @Override
        public void onError(Throwable e) {

        }

        @Override
        public void onNext(File file) {
            Host selectedHost = menu.getHostOnShow();
            if (selectedHost != null) {
                String expectContent = menu.getCommonContent() + form.getContent();
                try {
                    try {
                        FileSystem.os().changeHost(file, expectContent);
                        menu.changeSystemHost(expectContent);
                        selectedHost.setContent(form.getContent());
                        if (notOnlineHost(selectedHost)) {
                            helper.save(selectedHost);
                        }
                    } catch (InCorrectPasswordException e) {
                        form.showInCorrectPassword();
                    }

                } catch (IOException e1) {
                    showErrorMsg(e1.getMessage());
                }
            }
        }
    };

    private boolean notOnlineHost(Host selectedHost) {
        return !"online".equals(selectedHost.getType());
    }

    public void saveHost() {
        Host selectedHost = menu.getHostOnShow();
        if (selectedHost != null) {
            selectedHost.setContent(form.getContent());
            if (notOnlineHost(selectedHost)) {
                helper.save(selectedHost);
            } else {
                showErrorMsg("暂不提供远程host保存功能");
            }
        }
    }

    public void addHost(String name) {
        menu.updateHost(Host.local(name));
        tray.updateTray(name);
    }

    public void onTrayChange(String selectedHostName) {
        menu.changeHost(selectedHostName);
        showWindow();
    }

    public void onMenuChange(Host selectedNode) {
        form.changeHostContent(selectedNode.getContent());
        tray.changeHost(selectedNode.getName());
    }

    public void showWindow() {
        form.showWindow();
    }

    public void showErrorMsg(String msg) {
        form.showErrorMsg(msg);
    }

    public void destroy() {
        helper.destroy();
        if (!changeHostOnSubscriber.isUnsubscribed()) {
            changeHostOnSubscriber.unsubscribe();
        }
        if (!updateHostOnSubscriber.isUnsubscribed()) {
            updateHostOnSubscriber.unsubscribe();
        }
        System.out.println("销毁所有subscriber");
    }
}
