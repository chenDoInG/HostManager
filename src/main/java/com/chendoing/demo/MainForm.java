package com.chendoing.demo;

import com.chendoing.demo.component.DaggerMainFormComponent;

import javax.inject.Inject;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

@SuppressWarnings("unused")
public class MainForm {
    private JPanel rootPanel;

    @Inject
    JTree hosts;
    private JTextArea textArea1;
    private JButton button1;
    private JButton button2;
    private JButton button3;
    private JButton button4;
    private JScrollPane hostPanel;

    public MainForm() {
        JFrame frame = new JFrame("测试");
        frame.setContentPane(rootPanel);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
        frame.setJMenuBar(getMenu());
        button1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                textArea1.setText("测");
            }
        });
    }

    private JMenuBar getMenu() {
        JMenuBar menuBar = new JMenuBar();
        menuBar.add(new JMenu("测试"));
        return menuBar;
    }

    private void createUIComponents() {
        DaggerMainFormComponent.builder().build().injectMainForm(this);
    }
}
