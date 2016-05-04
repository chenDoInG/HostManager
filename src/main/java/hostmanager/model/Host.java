package hostmanager.model;

import org.joda.time.DateTime;

import javax.swing.tree.DefaultMutableTreeNode;

public class Host extends DefaultMutableTreeNode {

    private String name;
    private String content;

    private DateTime lastUpdateTime;

    private String type;

    public Host() {
    }

    public Host(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public DateTime getLastUpdateTime() {
        return lastUpdateTime;
    }

    public void setLastUpdateTime(DateTime lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
    }

    @Override
    public String toString() {
        return name;
    }

}
