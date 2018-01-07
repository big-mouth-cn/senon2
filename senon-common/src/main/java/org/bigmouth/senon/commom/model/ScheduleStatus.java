package org.bigmouth.senon.commom.model;

public enum ScheduleStatus {
    ON, OFF;

    public static ScheduleStatus get(int source) {
        for (ScheduleStatus e : values()) {
            if (e.ordinal() == source) {
                return e;
            }
        }
        return null;
    }
}
