package hostmanager.ui;

import hostmanager.model.Host;

import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.*;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

public class Menu extends JTree {

    private Map<String, Host> nodes = new HashMap<>();
    private MainForm form;

    private Host hostOnShow;

    public Menu(final MainForm form, Host node) {
        super(node);
        this.form = form;
        TreeSelectionModel model = getSelectionModel();
        model.setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        setSelectionModel(model);
        changeHost((Host) getModel().getRoot());
        addTreeSelectionListener(new TreeSelectionListener() {
            @Override
            public void valueChanged(TreeSelectionEvent e) {
                Host selectedNode = (Host) getLastSelectedPathComponent();
                if (selectedNode != null && selectedNode.isLeaf()) {
                    form.onMenuChange(selectedNode);
                    hostOnShow = selectedNode;
                }
            }
        });
    }

    public void changeSystemHost(String using) {
        nodes.get("当前系统host").setContent(using);
    }

    public String getCommonContent() {
        return "--------公共配置开始--------" + System.getProperty("line.separator") +
                nodes.get("公共配置").getContent() +
                "--------公共配置结束--------" + System.getProperty("line.separator") +
                System.getProperty("line.separator");
    }

    public Host getHostOnShow() {
        return hostOnShow;
    }

    public void removeHost() {
        if (hostOnShow != null) {
            DefaultTreeModel model = (DefaultTreeModel) getModel();
            Host parent = (Host) hostOnShow.getParent();
            if (parent != model.getRoot()) {
                model.removeNodeFromParent(hostOnShow);
                if (parent.getChildCount() == 0) {
                    model.removeNodeFromParent(parent);
                }
            } else {
                form.showErrorMsg("不能删除公共配置和当前系统host文件");
            }
        }
        updateUI();
    }

    public void updateHost(String parentName, Host host) {
        if (!nodes.containsKey(host.getName())) {
            DefaultTreeModel model = (DefaultTreeModel) this.getModel();
            Host parent = getNodeByHostName(parentName);
            model.insertNodeInto(host, parent, parent.getChildCount());
            nodes.put(host.getName(), host);
            expandTree();
        }
    }

    public void updateHost(Host host) {

        switch (host.getType()) {
            case "online":
                updateHost("在线方案", host);
                break;
            case "local":
                updateHost("本地方案", host);
                break;
            default:
                if (!nodes.containsKey(host.getName())) {
                    DefaultTreeModel model = (DefaultTreeModel) this.getModel();
                    model.insertNodeInto(host, (MutableTreeNode) model.getRoot(), ((MutableTreeNode) model.getRoot()).getChildCount());
                    expandTree();
                    nodes.put(host.getName(), host);
                } else {
                    nodes.put(host.getName(), host);
                }
        }
    }

    public void expandTree() {
        expandOrCollapseTree(new TreePath(getModel().getRoot()), true);
    }

    private void expandOrCollapseTree(TreePath parent, boolean expand) {
        TreeNode node = (TreeNode) parent.getLastPathComponent();
        if (!node.isLeaf()) {
            Enumeration children = node.children();
            while (children.hasMoreElements()) {
                expandOrCollapseTree(parent.pathByAddingChild(children.nextElement()), expand);
            }
        }
        if (expand) {
            expandPath(parent);
        } else {
            collapsePath(parent);
        }
    }

    public void changeHost(String selectedHostName) {
        if (nodes.containsKey(selectedHostName)) {
            changeHost(nodes.get(selectedHostName));
        }
    }

    public void changeHost(Host host) {
        TreePath selectedTreedPath = new TreePath(((DefaultTreeModel) getModel()).getPathToRoot(host));
        setSelectionPath(selectedTreedPath);
        scrollPathToVisible(selectedTreedPath);
    }

    private Host getNodeByHostName(String hostName) {
        Host node;
        if (nodes.containsKey(hostName)) {
            node = nodes.get(hostName);
        } else {
            node = new Host(hostName);
            updateHost(node);
        }
        return node;
    }

}
