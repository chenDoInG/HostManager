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
        return new Panel2AddHost(presenter);
    }

    @Singleton
    @Provides
    Menu provideMenu() {
        return new Menu(form, new Host("hosts"));
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
    Tray provideSystemTray() {
        return new Tray(form);
    }
}
