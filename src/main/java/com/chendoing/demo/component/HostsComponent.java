package com.chendoing.demo.component;

import com.chendoing.demo.module.HostsModule;
import com.chendoing.demo.present.HostPresent;
import com.chendoing.demo.ui.Hosts;
import dagger.Component;

import javax.inject.Singleton;

@Singleton
@Component(modules = HostsModule.class)
public interface HostsComponent {

    void inject(Hosts hosts);

    HostPresent present();
}
