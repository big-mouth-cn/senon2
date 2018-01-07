package org.bigmouth.senon.commom.zookeeper.listener;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import java.util.EventObject;

public class ChangeEventObject extends EventObject {

    private static final long serialVersionUID = 3947211309107126429L;

    public enum EventType {
        CHILD_ADDED, CHILD_REMOVED, CHILD_UPDATED;
    }

    private final EventType eventType;
    private final byte[] data;

    public static ChangeEventObject of(String path, EventType eventType, byte[] data) {
        return new ChangeEventObject(path, eventType, data);
    }

    private ChangeEventObject(Object source, EventType eventType, byte[] data) {
        super(source);
        if (!(source instanceof String))
            throw new IllegalArgumentException("path expect String,but " + source.getClass());
        this.eventType = eventType;
        this.data = data;
    }

    public String getPath() {
        return (String) getSource();
    }

    public EventType getEventType() {
        return eventType;
    }

    public byte[] getData() {
        return data;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.DEFAULT_STYLE);
    }
}
