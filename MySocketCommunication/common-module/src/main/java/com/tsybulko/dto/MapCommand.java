package com.tsybulko.dto;

/**
 * @author Vitalii Tsybulko
 * @version 1.0
 * @since 02/23/2016 14:07
 */
public enum MapCommand {

    put((byte) 1),
    get((byte) 2),
    clearAll((byte) 3);

    private final byte commandCode;
    private String key;
    private String value;

    MapCommand(byte commandCode) {
        this.commandCode = commandCode;
        this.value = null;
        this.key = null;
    }

    public static MapCommand getInstance(byte commandCode) {
        if (commandCode == put.getCommandCode()) {
            return put;
        } else if (commandCode == get.getCommandCode()) {
            return get;
        } else if (commandCode == clearAll.getCommandCode()) {
            return clearAll;
        } else {
            return null;
        }
    }

    public String getCommand() {
        return this.getValue();
    }

    public byte getCommandCode() {
        return commandCode;
    }

    public boolean isPut() {
        return this.equals(put);
    }

    public boolean isGet() {
        return this.equals(get);
    }

    public boolean isClear() {
        return this.equals(clearAll);
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public boolean isInitialised() {
        if (this.isClear()) {
            this.value = null;
            this.key = null;
            return true;
        } else if (this.isGet()) {
            this.value = null;
            return this.key != null;
        } else if (this.isPut()) {
            return this.key != null && this.value != null;
        } else {
            return false;
        }
    }
}
