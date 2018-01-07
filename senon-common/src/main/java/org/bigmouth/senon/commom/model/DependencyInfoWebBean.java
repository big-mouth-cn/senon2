package org.bigmouth.senon.commom.model;

import java.io.Serializable;

public class DependencyInfoWebBean implements Serializable {

    private String son;
    private String parent;

    public DependencyInfoWebBean() {
    }

    public DependencyInfoWebBean(String son, String parent) {
        this.son = son;
        this.parent = parent;
    }

    public String getSon() {
        return son;
    }

    public void setSon(String son) {
        this.son = son;
    }

    public String getParent() {
        return parent;
    }

    public void setParent(String parent) {
        this.parent = parent;
    }
}
