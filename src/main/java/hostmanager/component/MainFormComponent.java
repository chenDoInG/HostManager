package hostmanager.component;

import dagger.Component;
import hostmanager.module.MainFormModule;
import hostmanager.ui.MainForm;
import hostmanager.ui.Menu;
import hostmanager.ui.Panel2AddHost;
import hostmanager.ui.Tray;

import javax.inject.Singleton;

@Singleton
@Component(modules = MainFormModule.class)
public interface MainFormComponent {

    void inject(MainForm form);

    Menu menu();

    Tray tray();

    Panel2AddHost panel();
}
