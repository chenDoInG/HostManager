package hostmanager;

import hostmanager.ui.MainForm;

import java.awt.*;

public class HostManager {

    public static void main(String[] args){
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                new MainForm();
            }
        });
    }
}
