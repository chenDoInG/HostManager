package hostmanager.ui;

import hostmanager.component.DaggerMainFormComponent;
import hostmanager.model.Host;
import hostmanager.module.MainFormModule;
import hostmanager.presenter.MainFormPresenter;
import hostmanager.ui.button.RxButton;
import rx.functions.Action1;

import javax.inject.Inject;
import javax.swing.*;

public class MainForm {
    private JFrame frame;
    private JPanel rootPanel;
    private JTextArea hostContent;
    private JButton btn_add;
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
                       presenter.saveHost();
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
                        presenter.deleteHost();
                    }
                });
        RxButton.click(btn_active)
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        presenter.activeHost();
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

    public void changeHostContent(String content) {
        hostContent.setText(content);
        hostContent.setCaretPosition(0);
    }

    public String getContent(){
        return hostContent.getText();
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

    public void showInCorrectPassword(){
        JOptionPane.showMessageDialog(frame, "您输入的密码错误,请重新尝试", "密码提示", JOptionPane.ERROR_MESSAGE);
    }
}
