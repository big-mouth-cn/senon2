package org.bigmouth.senon.commom.model;

public enum ScheduleType {
    CRON, DEPENDENCY;

    public static ScheduleType get(int source) {
        for (ScheduleType e : values()) {
            if (e.ordinal() == source) {
                return e;
            }
        }
        return null;
    }
}
