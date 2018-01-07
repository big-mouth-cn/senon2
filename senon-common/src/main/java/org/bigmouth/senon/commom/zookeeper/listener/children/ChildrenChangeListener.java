package org.bigmouth.senon.commom.zookeeper.listener.children;

import org.bigmouth.senon.commom.zookeeper.listener.Change;
import org.bigmouth.senon.commom.zookeeper.listener.ChangeEventObject;
import org.bigmouth.senon.commom.zookeeper.listener.ChangeListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ChildrenChangeListener implements ChangeListener<ChangeEventObject> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ChildrenChangeListener.class);

    private final Change childrenChange;

    public ChildrenChangeListener(Change childrenChange) {
        if (null == childrenChange)
            throw new NullPointerException("childrenChange");
        this.childrenChange = childrenChange;
    }

    @Override
    public void onChanged(ChildrenMonitor monitor, ChangeEventObject event) {
        String path = event.getPath();
        byte[] data = event.getData();
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("ZooKeeper: " + event.getEventType() + " Path " + path);
        }
        switch (event.getEventType()) {
            case CHILD_ADDED: {
                childrenChange.add(monitor, path, data);
                break;
            }
            case CHILD_UPDATED: {
                childrenChange.update(monitor, path, data);
                break;
            }
            case CHILD_REMOVED: {
                childrenChange.remove(monitor, path, data);
                break;
            }
        }
    }
}
