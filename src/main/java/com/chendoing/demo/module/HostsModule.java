package com.chendoing.demo.module;

import com.chendoing.demo.api.GithubService;
import com.chendoing.demo.present.HostPresent;
import com.chendoing.demo.ui.Hosts;
import dagger.Module;
import dagger.Provides;
import retrofit2.Retrofit;

import javax.inject.Singleton;

@Module(includes = HttpModule.class)
public class HostsModule {

    private Hosts hosts;

    public HostsModule(Hosts hosts) {
        this.hosts = hosts;
    }

    @Singleton
    @Provides
    Hosts provideHosts() {
        return hosts;
    }

    @Singleton
    @Provides
    GithubService provideGithubService(Retrofit retrofit) {
        return retrofit.create(GithubService.class);
    }

    @Provides
    @Singleton
    HostPresent provideHostPresent(Hosts hosts, GithubService githubService) {
        return new HostPresent(hosts, githubService);
    }
}
