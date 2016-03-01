package com.tsybulko.dto.service;

import com.tsybulko.dto.MapCommand;
import org.apache.log4j.Logger;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;

/**
 * @author Vitalii Tsybulko
 * @version 1.0
 * @since 02/25/2016 12:05
 */
public class TransformerService {

    private static Logger logger = Logger.getLogger(TransformerService.class);

    private static final int HEADER_LENGTH = 5;
    private static final int KEYSIZE_LENGTH = 2;
    private static final int VALUESIZE_LENGTH = 2;
    private static final int KEYSIZE_OFFSET = 1;
    private static final int VALUESIZE_OFFSET = 3; // Is it necessary? Because this offset can be calculated;
    // in other case it must be reset manually every time when something changes in header

    private static final TransformerService INSTANCE = new TransformerService();

    private TransformerService() {
    }

    public static TransformerService getInstance() {
        return INSTANCE;
    }

    /**
     * Transforms byte array to appropriate command with arguments
     *
     * @param bytes byte array which need to be transformed
     * @return command after transformation
     */
    public MapCommand fromBytes(byte[] bytes) {
        MapCommand command = MapCommand.getInstance(bytes[0]);
        int keySize, valueSize;
        if (command.isGet()) {
            keySize = ByteBuffer.wrap(Arrays.copyOfRange(bytes, KEYSIZE_OFFSET, KEYSIZE_OFFSET + KEYSIZE_LENGTH)).getShort();
            checkSum(bytes, keySize);
            command.setKey(new String(Arrays.copyOfRange(bytes, HEADER_LENGTH, HEADER_LENGTH + keySize)));
        } else if (command.isPut()) {
            keySize = ByteBuffer.wrap(Arrays.copyOfRange(bytes, KEYSIZE_OFFSET, KEYSIZE_OFFSET + KEYSIZE_LENGTH)).getShort();
            valueSize = ByteBuffer.wrap(Arrays.copyOfRange(bytes, KEYSIZE_OFFSET + KEYSIZE_LENGTH, KEYSIZE_OFFSET + KEYSIZE_LENGTH + VALUESIZE_LENGTH)).getShort();
            checkSum(bytes, keySize + valueSize);
            command.setKey(new String(Arrays.copyOfRange(bytes, HEADER_LENGTH, HEADER_LENGTH + keySize)));
            command.setValue(new String(Arrays.copyOfRange(bytes, HEADER_LENGTH + keySize, HEADER_LENGTH + keySize + valueSize)));
        }
        return command;
    }

    private void checkSum(byte[] bytes, int bodyLength) {
        if (bytes.length - HEADER_LENGTH != bodyLength) {
            logger.error("Check sum of TCP package, maybe it is corrupted.");
            System.exit(1);
        }
    }

    /**
     * Transforms command to the byte interpretation
     *
     * @param command command which need to be transformed
     * @return array of bytes which consists of command type, arguments lengths in header and then command arguments
     */
    public byte[] toBytes(MapCommand command) {
        ByteArrayOutputStream bOutput = new ByteArrayOutputStream();
        ByteBuffer dBuf = ByteBuffer.allocate(4);
        byte[] header = new byte[0];
        byte[] body = new byte[0];
        try {
            bOutput.write(command.getCommandCode());
            if (command.isGet()) {
                byte[] key = command.getKey().getBytes();
                dBuf.putChar((char) key.length);
                dBuf.putChar((char) 0);
                body = key;
            } else if (command.isPut()) {
                byte[] key = command.getKey().getBytes();
                byte[] value = command.getValue().getBytes();
                dBuf.putChar((char) key.length);
                dBuf.putChar((char) value.length);
                body = (command.getKey() + command.getValue()).getBytes();
            }
            bOutput.write(dBuf.array());
            header = bOutput.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return mergeArrays(header, body);
    }

    private byte[] mergeArrays(byte[] a, byte[] b) {
        int length = a.length + b.length;
        byte[] result = new byte[length];
        System.arraycopy(a, 0, result, 0, a.length);
        System.arraycopy(b, 0, result, a.length, b.length);
        return result;
    }

}
