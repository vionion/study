package com.tsybulko.dto;

import com.tsybulko.dto.command.MapCommand;
import com.tsybulko.dto.command.MapCommandDTO;
import org.apache.log4j.Logger;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.HashMap;
import java.util.zip.CRC32;
import java.util.zip.Checksum;

/**
 * @author Vitalii Tsybulko
 * @version 1.0
 * @since 02/25/2016 12:05
 */
public class TransformerService {

    private static Logger logger = Logger.getLogger(TransformerService.class);

    public static final int HEADER_LENGTH = 13;
    public static final int CHECKSUM_LENGTH = 8;
    public static final int CHECKSUM_OFFSET = 5;
    public static final int KEYSIZE_LENGTH = 2;
    public static final int VALUESIZE_LENGTH = 2;
    public static final int KEYSIZE_OFFSET = 1;
    public static final int VALUESIZE_OFFSET = 3;

    private static final TransformerService INSTANCE = new TransformerService();

    private TransformerService() {
    }

    public static TransformerService getInstance() {
        return INSTANCE;
    }

    /**
     * Transforms byte array to appropriate command with arguments
     *
     * @param header    byte array which need to be transformed
     * @param allErrors
     * @return command after transformation
     */
    public MapCommandDTO fromBytes(byte[] header, byte[] data, HashMap<String, String> allErrors) {
        MapCommandDTO command = new MapCommandDTO(MapCommand.getInstance(header[0]));
        int keySize, valueSize;
        if (command.isGet()) {
            keySize = ByteBuffer.wrap(Arrays.copyOfRange(header, KEYSIZE_OFFSET, KEYSIZE_OFFSET + KEYSIZE_LENGTH)).getShort();
            if (!isPackageHolistic(header, data, allErrors)) {
                return null;
            }
            command.setKey(new String(Arrays.copyOfRange(data, 0, keySize)));
        } else if (command.isPut()) {
            keySize = ByteBuffer.wrap(Arrays.copyOfRange(header, KEYSIZE_OFFSET, KEYSIZE_OFFSET + KEYSIZE_LENGTH)).getShort();
            valueSize = ByteBuffer.wrap(Arrays.copyOfRange(header, KEYSIZE_OFFSET + KEYSIZE_LENGTH, KEYSIZE_OFFSET + KEYSIZE_LENGTH + VALUESIZE_LENGTH)).getShort();
            if (!isPackageHolistic(header, data, allErrors)) {
                return null;
            }
            command.setKey(new String(Arrays.copyOfRange(data, 0, keySize)));
            command.setValue(new String(Arrays.copyOfRange(data, keySize, keySize + valueSize)));
        }
        return command;
    }

    /**
     * Calculates length of data part of package from header
     * @param bytes array of header bytes
     * @return length of data
     */
    public int getDataPartSize(byte[] bytes) {
        MapCommand command = MapCommand.getInstance(bytes[0]);
        int keySize, valueSize;
        if (command == MapCommand.get) {
            keySize = ByteBuffer.wrap(Arrays.copyOfRange(bytes, KEYSIZE_OFFSET, KEYSIZE_OFFSET + KEYSIZE_LENGTH)).getShort();
            return keySize;
        } else if (command == MapCommand.put) {
            keySize = ByteBuffer.wrap(Arrays.copyOfRange(bytes, KEYSIZE_OFFSET, KEYSIZE_OFFSET + KEYSIZE_LENGTH)).getShort();
            valueSize = ByteBuffer.wrap(Arrays.copyOfRange(bytes, KEYSIZE_OFFSET + KEYSIZE_LENGTH, KEYSIZE_OFFSET + KEYSIZE_LENGTH + VALUESIZE_LENGTH)).getShort();
            return keySize + valueSize;
        } else {
            return 0;
        }
    }

    /**
     * Checks checksum of package
     * @param header header of package
     * @param data data of package
     * @param allErrors map for errors
     * @return result of checking integrity of package
     */
    private boolean isPackageHolistic(byte[] header, byte[] data, HashMap<String, String> allErrors) {
        Checksum checksum = new CRC32();
        checksum.update(data, 0, data.length);
        if (checksum.getValue() != ByteBuffer.wrap(Arrays.copyOfRange(header, CHECKSUM_OFFSET, CHECKSUM_OFFSET + CHECKSUM_LENGTH)).getLong()) {
            logger.error("Check sum of TCP package, maybe it is corrupted.");
            allErrors.put("transformer", "Check sum of TCP package, maybe it is corrupted.");
            return false;
        }
        return true;
    }

    /**
     * Transforms command to the byte interpretation
     *
     * @param command command which need to be transformed
     * @return array of bytes which consists of command type, arguments lengths in header and then command arguments
     */
    public byte[] toBytes(MapCommandDTO command) {
        ByteArrayOutputStream bOutput = new ByteArrayOutputStream();
        ByteBuffer dBuf = ByteBuffer.allocate(HEADER_LENGTH - 1);
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
            Checksum checksum = new CRC32();
            checksum.update(body, 0, body.length);
            dBuf.putLong(checksum.getValue());
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
