package hostmanager.ui;

import hostmanager.component.DaggerMainFormComponent;
import hostmanager.enums.FileSystem;
import hostmanager.model.Host;
import hostmanager.module.MainFormModule;
import hostmanager.presenter.MainFormPresenter;
import rx.functions.Action1;

import javax.inject.Inject;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.List;

public class MainForm {
    private JFrame frame;
    private JPanel rootPanel;
    private JTextArea hostContent;
    private JButton btn_add;
    private JButton btn_refresh;
    private JButton btn_modify;
    private JButton btn_del;
    private JButton btn_save;
    private JButton btn_active;
    private JScrollPane menuScrollPane;
    @Inject
    Menu menu;
    @Inject
    MainFormPresenter presenter;
    @Inject
    Tray tray;
    @Inject
    Panel2AddHost panel2AddHost;

    public MainForm() {
        initUI();
        initPresent();
        initButtonListener();
    }

    private void initButtonListener() {
        btn_save.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Host selectedHost = menu.getHostOnShow();
                if (selectedHost != null) {
                    selectedHost.setContent(hostContent.getText());
                    presenter.saveHost(selectedHost);
                }
            }
        });
        btn_add.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                panel2AddHost.show();
            }
        });
        btn_del.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Host selectedHost = menu.getHostOnShow();
                if (selectedHost != null) {
                    menu.removeHost();
                    tray.deleteTray(selectedHost.getName());
                    presenter.deleteHost(selectedHost);
                } else {
                    showErrorMsg("您还没有选择任何host");
                }
            }
        });
        btn_active.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                FileSystem.os().getHostFile()
                        .subscribe(new Action1<File>() {
                            @Override
                            public void call(File file) {
                                Host selectedHost = menu.getHostOnShow();
                                if (selectedHost != null) {
                                    try {
                                        selectedHost.setContent(hostContent.getText());
                                        menu.changeSystemHost();
                                        FileSystem.os().changeHost(file, menu.getSystemHost().getContent());
                                    } catch (IOException e1) {
                                        showErrorMsg(e1.getMessage());
                                    }
                                }
                            }
                        });
            }
        });
    }

    private void createUIComponents() {
        DaggerMainFormComponent.builder().mainFormModule(new MainFormModule(this)).build().inject(this);
        menuScrollPane = new JScrollPane(menu);
    }

    private void initUI() {
        frame = new JFrame("HostManager");
        frame.setContentPane(rootPanel);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private void initPresent() {
        presenter.askForNewData();

    }

    private void changeHostContent(String content) {
        hostContent.setText(content);
    }

    public void onMenuChange(Host selectedNode) {
        changeHostContent(selectedNode.getContent());
        tray.changeHost(selectedNode.getName());

    }

    public void onTrayChange(String selectedHostName) {
        menu.changeHost(selectedHostName);
    }

    public void updateHost(Host host) {
        menu.updateHost(host);
        tray.updateTray(host.getName());
    }

    public void updateHost(String host) {
        menu.updateHost(host);
        tray.updateTray(host);
    }

    public void updateHost(String parentName, Host host) {
        menu.updateHost(parentName, host);
        tray.updateTray(host.getName());
    }

    public void updateHosts(String menuName, List<String> hosts) {
        menu.updateHosts(menuName, hosts);
        tray.updateTrays(hosts);
    }

    public void showWindow() {
        frame.setExtendedState(JFrame.NORMAL);
    }

    public void showErrorMsg(String errorMsg) {
        JOptionPane.showMessageDialog(frame, errorMsg);
    }

    public String showPasswordDialog() {
        return JOptionPane.showInputDialog(frame, "请输入密码:");
    }
}
