package com.tsybulko.dto.service;

import com.tsybulko.common.TestWithLogger;
import com.tsybulko.dto.response.ResponseDTO;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author Vitalii Tsybulko
 * @version 1.0
 * @since 02/29/2016 12:07
 */
public class ResponseTransformerServiceTest extends TestWithLogger {

    private ResponseTransformerService transformer = ResponseTransformerService.getInstance();

    @Test
    public void testToBytesFromBytes() throws Exception {
        HashMap<String, String> allErrors = new HashMap<String, String>();
        byte[] responseByte = transformer.toBytes(new ResponseDTO(true, "test"));
        ResponseDTO transformed = transformer.fromBytes(Arrays.copyOfRange(responseByte, 0, ResponseTransformerService.HEADER_LENGTH), Arrays.copyOfRange(responseByte, ResponseTransformerService.HEADER_LENGTH, responseByte.length), allErrors);
        assertEquals("test", transformed.getAnswer());
        assertTrue(allErrors.isEmpty());
        assertTrue(transformed.isSuccess());
    }

    @Test
    public void testToBytesFromBytesLongNames() throws Exception {
        ResponseDTO longResponse = new ResponseDTO(true, "test");
        StringBuilder longAnswer = new StringBuilder();
        int i = 0;
        while (longAnswer.toString().getBytes().length < Short.MAX_VALUE - 7) {
            longAnswer.append("ans" + i);
            i++;
        }
        longResponse.setAnswer(longAnswer.toString());
        HashMap<String, String> allErrors = new HashMap<String, String>();
        byte[] longResponseByte = transformer.toBytes(longResponse);
        ResponseDTO transformed = transformer.fromBytes(Arrays.copyOfRange(longResponseByte, 0, ResponseTransformerService.HEADER_LENGTH), Arrays.copyOfRange(longResponseByte, ResponseTransformerService.HEADER_LENGTH, longResponseByte.length), allErrors);
        assertTrue(allErrors.isEmpty());
        assertEquals(longResponse.getAnswer(), transformed.getAnswer());
        assertTrue(transformed.isSuccess());
    }

    @Test
    public void testTransformCorruptedBytes() throws Exception {
        HashMap<String, String> allErrors = new HashMap<String, String>();
        ResponseDTO response = new ResponseDTO(true, "test");
        byte[] responseByte = transformer.toBytes(response);
        responseByte[ResponseTransformerService.HEADER_LENGTH - 1] += 1;
        transformer.fromBytes(Arrays.copyOfRange(responseByte, 0, ResponseTransformerService.HEADER_LENGTH), Arrays.copyOfRange(responseByte, ResponseTransformerService.HEADER_LENGTH, responseByte.length), allErrors);
        assertTrue(!allErrors.isEmpty());
    }
}
