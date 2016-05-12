package hostmanager.module;

import dagger.Module;
import dagger.Provides;
import hostmanager.api.MenuService;
import hostmanager.helper.HostHelper;
import hostmanager.model.Host;
import hostmanager.presenter.MainFormPresenter;
import hostmanager.ui.MainForm;
import hostmanager.ui.Menu;
import hostmanager.ui.Panel2AddHost;
import hostmanager.ui.Tray;
import retrofit2.Retrofit;

import javax.inject.Singleton;

@Module(includes = {HttpModule.class})
public class MainFormModule {

    private MainForm form;

    public MainFormModule(MainForm form) {
        this.form = form;
    }

    @Singleton
    @Provides
    Panel2AddHost providePanel(MainFormPresenter presenter) {
        Panel2AddHost panel2AddHost = new Panel2AddHost(presenter);
        presenter.injectPanel(panel2AddHost);
        return panel2AddHost;
    }

    @Singleton
    @Provides
    Menu provideMenu(MainFormPresenter presenter) {
        Menu menu = new Menu(presenter, new Host("hosts"));
        presenter.injectMenu(menu);
        return menu;
    }

    @Singleton
    @Provides
    HostHelper provideHelper() {
        return new HostHelper();
    }

    @Singleton
    @Provides
    MenuService provideMenuService(Retrofit retrofit) {
        return retrofit.create(MenuService.class);
    }

    @Provides
    @Singleton
    MainFormPresenter provideMainFormPresent(MenuService menuService, HostHelper helper) {
        return new MainFormPresenter(form, menuService, helper);
    }

    @Singleton
    @Provides
    Tray provideSystemTray(MainFormPresenter presenter) {
        Tray tray = new Tray(presenter);
        presenter.injectTray(tray);
        return tray;
    }
}
