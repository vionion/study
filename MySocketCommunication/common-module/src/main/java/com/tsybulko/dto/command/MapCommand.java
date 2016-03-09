package com.tsybulko.dto.command;

/**
 * @author Vitalii Tsybulko
 * @version 1.0
 * @since 03/04/2016 16:45
 */
public enum MapCommand {

    put((byte) 1),
    get((byte) 2),
    clearAll((byte) 3);

    private final byte commandCode;

    MapCommand(byte commandCode) {
        this.commandCode = commandCode;
    }

    public byte getCommandCode() {
        return commandCode;
    }

    public static MapCommand getInstance(byte commandCode) {
        for (MapCommand mapCommand : values()) {
            if (mapCommand.getCommandCode() == commandCode) {
                return mapCommand;
            }
        }
        return null;
    }
}
