package hostmanager.ui;

import hostmanager.component.DaggerMainFormComponent;
import hostmanager.enums.FileSystem;
import hostmanager.exception.InCorrectPasswordException;
import hostmanager.model.Host;
import hostmanager.module.MainFormModule;
import hostmanager.presenter.MainFormPresenter;
import hostmanager.ui.button.RxButton;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

import javax.inject.Inject;
import javax.swing.*;
import java.io.File;
import java.io.IOException;

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
        RxButton.click(btn_save)
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        Host selectedHost = menu.getHostOnShow();
                        if (selectedHost != null) {
                            selectedHost.setContent(hostContent.getText());
                            if (!"online".equals(selectedHost.getType())) {
                                presenter.saveHost(selectedHost);
                            } else {
                                showErrorMsg("暂不提供远程host保存功能");
                            }
                        }
                    }
                });
        RxButton.click(btn_add)
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        panel2AddHost.show();
                    }
                });

        RxButton.click(btn_del)
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        Host selectedHost = menu.getHostOnShow();
                        if (selectedHost != null) {
                            if (!"online".equals(selectedHost.getType())) {
                                menu.removeHost();
                                tray.deleteTray(selectedHost.getName());
                                presenter.deleteHost(selectedHost);
                            } else {
                                showErrorMsg("暂不提供远程host删除功能");
                            }

                        } else {
                            showErrorMsg("您还没有选择任何host");
                        }
                    }
                });
        RxButton.click(btn_active)
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        FileSystem.os().getHostFile()
                                .subscribeOn(Schedulers.io())
                                .subscribe(new Action1<File>() {
                                    @Override
                                    public void call(File file) {
                                        Host selectedHost = menu.getHostOnShow();
                                        if (selectedHost != null) {
                                            String expectContent = menu.getCommonContent() + hostContent.getText();
                                            try {
                                                try {
                                                    FileSystem.os().changeHost(file, expectContent);
                                                    selectedHost.setContent(hostContent.getText());
                                                    presenter.saveHost(selectedHost);
                                                    menu.changeSystemHost(expectContent);
                                                } catch (InCorrectPasswordException e) {
                                                    JOptionPane.showMessageDialog(frame, "您输入的密码错误,请重新尝试", "密码提示", JOptionPane.ERROR_MESSAGE);
                                                }

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
        hostContent.setCaretPosition(0);
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

    public void showWindow() {
        frame.setExtendedState(JFrame.NORMAL);
    }

    public void showErrorMsg(String errorMsg) {
        JOptionPane.showMessageDialog(frame, errorMsg);
    }

}
