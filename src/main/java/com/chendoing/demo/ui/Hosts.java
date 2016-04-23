package com.chendoing.demo.ui;

import com.chendoing.demo.component.DaggerHostsComponent;
import com.chendoing.demo.model.Repos;
import com.chendoing.demo.module.HostsModule;
import com.chendoing.demo.present.HostPresent;

import javax.inject.Inject;
import javax.swing.*;
import javax.swing.tree.*;
import java.util.List;

public class Hosts {

    private JTree menu;

    @Inject
    HostPresent present;

    private DefaultMutableTreeNode loading;

    public Hosts() {
        DaggerHostsComponent.builder().hostsModule(new HostsModule(this)).build().inject(this);
        DefaultMutableTreeNode parent = new DefaultMutableTreeNode("repos");
        menu = new JTree(parent);
        present.onCreate();
    }

    public JTree getMenu() {
        return menu;
    }

    public void showErrorMessage() {
        JOptionPane.showMessageDialog(menu, "服务器错误");
    }

    public void updateHosts(List<Repos> hosts) {
        DefaultTreeModel model = (DefaultTreeModel) menu.getModel();
        for (Repos host : hosts) {
            model.insertNodeInto(new DefaultMutableTreeNode(host.getName()), (MutableTreeNode) model.getRoot(), 0);
        }
        menu.expandPath(new TreePath(model.getRoot()));
    }

    public void showLoadingMessage() {
        loading = new DefaultMutableTreeNode("加载中……");
        DefaultTreeModel model = (DefaultTreeModel) menu.getModel();
        model.insertNodeInto(loading, (MutableTreeNode) model.getRoot(), 0);
        menu.expandPath(new TreePath(model.getRoot()));
    }

    public void hideLoadingMessage() {
        DefaultTreeModel model = (DefaultTreeModel) menu.getModel();
        model.removeNodeFromParent(loading);
    }
}
