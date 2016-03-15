package com.tsybulko.dto.command;

import com.tsybulko.dto.IDTO;

/**
 * @author Vitalii Tsybulko
 * @version 1.0
 * @since 02/23/2016 14:07
 */
public class MapCommandDTO implements IDTO {

    private final MapCommand command;
    private String key;
    private String value;

    public MapCommandDTO(MapCommand command) {
        this.command = command;
        this.value = null;
        this.key = null;
    }

    public MapCommand getCommand() {
        return command;
    }

    public byte getCommandCode() {
        return command.getCommandCode();
    }

    public boolean isPut() {
        return this.command == MapCommand.put;
    }

    public boolean isGet() {
        return this.command == MapCommand.get;
    }

    public boolean isClear() {
        return this.command == MapCommand.clearAll;
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
