package hostmanager.ui;

import hostmanager.model.Host;
import hostmanager.presenter.MainFormPresenter;
import hostmanager.ui.button.RxButton;
import org.apache.commons.lang3.StringUtils;
import rx.functions.Action1;

import javax.swing.*;

public class Panel2AddHost {
    private JPanel panel2AddHost;
    private JButton btn_save_local;
    private JTextField localName;
    private JButton btn_save_remote;
    private JTextField remoteName;
    private JTextField remoteHost;
    private JFrame frame;

    MainFormPresenter presenter;

    public Panel2AddHost(MainFormPresenter presenter) {
        this.presenter = presenter;
        initUI();
        initButtonListener();
    }

    private void initUI() {
        frame = new JFrame("Panel2AddHost");
        frame.setContentPane(panel2AddHost);
        frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(false);
    }

    private void initButtonListener() {
        RxButton.click(btn_save_local)
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        if (StringUtils.isNotEmpty(localName.getText())) {
                            presenter.saveHost(Host.local(localName.getText()));
                        } else {
                            showMsg("方案名称不能为空");
                        }
                    }
                });
    }

    private void showMsg(String content) {
        JOptionPane.showMessageDialog(frame, content);
    }

    public void show() {
        if (!frame.isVisible())
            frame.setVisible(true);
    }
}
