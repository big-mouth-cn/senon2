package org.bigmouth.senon.commom.model;

public enum JobType {
    SHELL, HIVE, PYTHON;

    public static JobType get(int source) {
        for (JobType e : values()) {
            if (e.ordinal() == source) {
                return e;
            }
        }
        return null;
    }

}
