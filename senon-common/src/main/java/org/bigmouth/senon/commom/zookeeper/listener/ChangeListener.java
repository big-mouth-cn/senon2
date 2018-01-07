package org.bigmouth.senon.commom.zookeeper.listener;


import org.bigmouth.senon.commom.zookeeper.listener.children.ChildrenMonitor;

import java.util.EventListener;


public interface ChangeListener<E> extends EventListener {

    public void onChanged(ChildrenMonitor client, E event);
}
