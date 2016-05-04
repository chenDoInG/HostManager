package hostmanager.component;

import dagger.Component;
import hostmanager.helper.HostHelper;
import hostmanager.module.HostHelperModule;
import hostmanager.util.DbUtil;

import javax.inject.Singleton;

@Singleton
@Component(modules = HostHelperModule.class)
public interface HostHelperComponent {

    void inject(HostHelper helper);

    DbUtil db();
}
