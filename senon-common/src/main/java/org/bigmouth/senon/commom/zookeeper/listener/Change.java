package org.bigmouth.senon.commom.zookeeper.listener;


import org.bigmouth.senon.commom.zookeeper.listener.children.ChildrenMonitor;

public interface Change {

    void add(ChildrenMonitor monitor, String path, byte[] data);
    
    void update(ChildrenMonitor monitor, String path, byte[] data);
    
    void remove(ChildrenMonitor monitor, String path, byte[] data);
}
