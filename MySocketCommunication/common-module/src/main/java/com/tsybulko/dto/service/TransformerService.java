package com.tsybulko.dto.service;

import com.tsybulko.dto.IDTO;
import org.apache.log4j.Logger;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.HashMap;
import java.util.zip.CRC32;
import java.util.zip.Checksum;

/**
 * @author Vitalii Tsybulko
 * @version 1.0
 * @since 03/15/2016 13:20
 */
public abstract class TransformerService {

    private static Logger logger = Logger.getLogger(TransformerService.class);

    /**
     * Checks checksum of package
     *
     * @param header    header of package
     * @param data      data of package
     * @param allErrors map for errors
     * @return result of checking integrity of package
     */
    protected boolean isPackageHolistic(byte[] header, byte[] data, HashMap<String, String> allErrors, int checksumOffset, int checksumLength) {
        Checksum сhecksumModel = new CRC32();
        сhecksumModel.update(data, 0, data.length);
        if (сhecksumModel.getValue() != ByteBuffer.wrap(Arrays.copyOfRange(header, checksumOffset, checksumOffset + checksumLength)).getLong()) {
            logger.error("Check sum of TCP package, maybe it is corrupted.");
            allErrors.put("transformer", "Check sum of TCP package, maybe it is corrupted.");
            return false;
        }
        return true;
    }

    public abstract IDTO fromBytes(byte[] header, byte[] data, HashMap<String, String> allErrors);

    public abstract int getDataPartSize(byte[] bytes);

    protected byte[] mergeArrays(byte[] a, byte[] b) {
        int length = a.length + b.length;
        byte[] result = new byte[length];
        System.arraycopy(a, 0, result, 0, a.length);
        System.arraycopy(b, 0, result, a.length, b.length);
        return result;
    }

}
