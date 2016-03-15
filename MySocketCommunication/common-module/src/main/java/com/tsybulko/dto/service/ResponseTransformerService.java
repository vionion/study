package com.tsybulko.dto.service;

import com.tsybulko.dto.response.ResponseDTO;
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
public class ResponseTransformerService extends TransformerService {

    private static Logger logger = Logger.getLogger(ResponseTransformerService.class);

    public static final int HEADER_LENGTH = 11;
    public static final int CHECKSUM_LENGTH = 8;
    public static final int CHECKSUM_OFFSET = 3;
    public static final int ANSWERSIZE_LENGTH = 2;
    public static final int ANSWERSIZE_OFFSET = 1;

    private static final ResponseTransformerService INSTANCE = new ResponseTransformerService();

    private ResponseTransformerService() {
    }

    public static ResponseTransformerService getInstance() {
        return INSTANCE;
    }

    /**
     * Transforms byte array to appropriate response with answer
     *
     * @param header    byte array which need to be transformed
     * @param allErrors
     * @return response after transformation
     */
    @Override
    public ResponseDTO fromBytes(byte[] header, byte[] data, HashMap<String, String> allErrors) {
        int answerSize = ByteBuffer.wrap(Arrays.copyOfRange(header, ANSWERSIZE_OFFSET, ANSWERSIZE_OFFSET + ANSWERSIZE_LENGTH)).getShort();
        if (!isPackageHolistic(header, data, allErrors, CHECKSUM_OFFSET, CHECKSUM_LENGTH)) {
            return null;
        }
        String answer = new String(Arrays.copyOfRange(data, 0, answerSize));
        return new ResponseDTO(header[0] != 0, answer);
    }

    /**
     * Calculates length of data part of package from header
     *
     * @param bytes array of header bytes
     * @return length of data
     */
    @Override
    public int getDataPartSize(byte[] bytes) {
        return ByteBuffer.wrap(Arrays.copyOfRange(bytes, ANSWERSIZE_OFFSET, ANSWERSIZE_OFFSET + ANSWERSIZE_LENGTH)).getShort();
    }

    /**
     * Transforms response to the byte interpretation
     *
     * @param response response which need to be transformed
     * @return array of bytes which consists of response success, checksum, answer lengths in header and then answer or error message
     */
    public byte[] toBytes(ResponseDTO response) {
        ByteArrayOutputStream bOutput = new ByteArrayOutputStream();
        ByteBuffer dBuf = ByteBuffer.allocate(HEADER_LENGTH - 1);
        byte[] header = new byte[0];
        byte[] answer = response.getAnswer().getBytes();
        try {
            bOutput.write(response.isSuccess() ? (byte) 1 : (byte) 0);
            dBuf.putChar((char) answer.length);
            Checksum checksum = new CRC32();
            checksum.update(answer, 0, answer.length);
            dBuf.putLong(checksum.getValue());
            bOutput.write(dBuf.array());
            header = bOutput.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return mergeArrays(header, answer);
    }

}
