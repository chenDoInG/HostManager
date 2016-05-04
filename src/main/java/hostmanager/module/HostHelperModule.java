package hostmanager.module;

import dagger.Module;
import dagger.Provides;
import hostmanager.util.DbUtil;

import javax.inject.Singleton;

@Module
public class HostHelperModule {

    @Singleton
    @Provides
    public DbUtil provideDb() {
        return new DbUtil();
    }
}
