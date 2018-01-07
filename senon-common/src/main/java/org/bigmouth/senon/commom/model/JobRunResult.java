package org.bigmouth.senon.commom.model;

public enum JobRunResult {
    RUNNING, SUCCESS, FAILED;

    public static JobRunResult get(int source) {
        for (JobRunResult e : values()) {
            if (e.ordinal() == source) {
                return e;
            }
        }
        return null;
    }
}
