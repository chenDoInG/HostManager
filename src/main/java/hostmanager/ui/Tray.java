package hostmanager.ui;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Tray {

    private SystemTray tray = SystemTray.getSystemTray();
    private TrayIcon trayIcon = getTrayIcon();
    private Map<String, MenuItem> menuItems = new HashMap<>();
    private MainForm form;
    private MenuItem selectedItem;

    public Tray(MainForm form) {
        this.form = form;
        initSystemTray();
        initOperationTray();
    }

    private void initOperationTray() {
        MenuItem about = new MenuItem("关于");
        about.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });
        MenuItem quit = new MenuItem("退出");
        quit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        trayIcon.getPopupMenu().add(about);
        trayIcon.getPopupMenu().add(quit);
    }

    private void initSystemTray() {
        try {
            tray.add(trayIcon);
        } catch (AWTException e) {
            e.printStackTrace();
        }
    }

    public void changeHost(String hostName) {
        String selectedName = "√ " + hostName;
        if (menuItems.containsKey(hostName)) {
            MenuItem item = menuItems.get(hostName);
            if (item != selectedItem) {
                if (selectedItem != null) {
                    selectedItem.setLabel(selectedItem.getLabel().replace("√ ", ""));
                }
                item.setLabel(selectedName);
                selectedItem = item;
                form.onTrayChange(hostName);
                form.showWindow();
            }
        }
    }

    public void updateTrays(List<String> hostNames) {
        for (String hostName : hostNames) {
            addMenuItem(hostName);
        }
        addSeparator();
    }

    public void updateTray(String hostName) {
        addMenuItem(hostName);
        addSeparator();
    }

    private void addMenuItem(final String hostName) {
        final MenuItem item;
        if (!menuItems.containsKey(hostName)) {
            item = new MenuItem(hostName);
            item.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    changeHost(hostName);
                }
            });
            menuItems.put(hostName, item);
            PopupMenu menu = trayIcon.getPopupMenu();
            menu.insert(item, menu.getItemCount() - 2);
        }
    }

    public void deleteTray(String hostName) {
        if (menuItems.containsKey(hostName)) {
            trayIcon.getPopupMenu().remove(menuItems.get(hostName));
            menuItems.remove(hostName);
        }
    }

    public void addSeparator() {
        PopupMenu menu = trayIcon.getPopupMenu();
        menu.insertSeparator(menu.getItemCount() - 2);
    }

    private TrayIcon getTrayIcon() {
        TrayIcon icon = new TrayIcon(getIconImage(), "HostManager", new PopupMenu("测试"));
        icon.setImageAutoSize(true);
        icon.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {

            }

            @Override
            public void mousePressed(MouseEvent e) {
                form.showWindow();
            }

            @Override
            public void mouseReleased(MouseEvent e) {

            }

            @Override
            public void mouseEntered(MouseEvent e) {

            }

            @Override
            public void mouseExited(MouseEvent e) {

            }
        });
        return icon;
    }

    private Image getIconImage() {
        return Toolkit.getDefaultToolkit().getImage(getClass().getResource("/desktop.png"));
    }
}
