package com.chendoing.demo.module;

import com.chendoing.demo.ui.Hosts;
import dagger.Module;
import dagger.Provides;

import javax.inject.Singleton;
import javax.swing.*;

@Module
public class MainFormModule {

    @Singleton
    @Provides
    JTree provideHosts() {
        return new Hosts().getMenu();
    }
}
