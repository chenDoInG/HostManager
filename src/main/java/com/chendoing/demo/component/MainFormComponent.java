package com.chendoing.demo.component;

import com.chendoing.demo.MainForm;
import com.chendoing.demo.module.MainFormModule;
import dagger.Component;

import javax.inject.Singleton;
import javax.swing.*;

@Singleton
@Component(modules = MainFormModule.class)
public interface MainFormComponent {

    void injectMainForm(MainForm mainForm);

    JTree hosts();
}
