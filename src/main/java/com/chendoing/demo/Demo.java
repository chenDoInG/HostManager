package com.chendoing.demo;

import java.awt.*;

public class Demo {

    public static void main(String args[]) {
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                MainForm form = new MainForm();
            }
        });
    }
}
