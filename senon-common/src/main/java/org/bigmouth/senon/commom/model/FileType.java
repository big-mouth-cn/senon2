package org.bigmouth.senon.commom.model;

public enum FileType {
    FILE(0), FOLDER(1);
    private int type;

    FileType(int type) {
        this.type = type;
    }

    public static FileType fromInteger(int type) {
        return type == 0 ? FileType.FILE : FileType.FOLDER;
    }

    public static FileType get(int source) {
        for (FileType e : values()) {
            if (e.ordinal() == source) {
                return e;
            }
        }
        return null;
    }

}
