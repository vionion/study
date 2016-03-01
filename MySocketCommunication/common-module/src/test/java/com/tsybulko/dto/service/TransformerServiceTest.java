package com.tsybulko.dto.service;

import common.TestWithLogger;
import com.tsybulko.dto.MapCommand;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author Vitalii Tsybulko
 * @version 1.0
 * @since 02/29/2016 12:07
 */
public class TransformerServiceTest extends TestWithLogger {

    private TransformerService transformer = TransformerService.getInstance();

    @Test
    public void testToBytesFromBytes() throws Exception {
        assertSame(MapCommand.clearAll, transformer.fromBytes(transformer.toBytes(MapCommand.clearAll)));
    }

    @Test
    public void testToBytesFromBytesLongNames() throws Exception {
        MapCommand longPutCommand = MapCommand.put;
        StringBuilder longKey = new StringBuilder();
        StringBuilder longValue = new StringBuilder();
        int i = 0;
        while (longKey.toString().getBytes().length < Short.MAX_VALUE - 7) {
            longKey.append("key" + i);
            longValue.append("val" + i);
            i++;
        }
        longPutCommand.setKey(longKey.toString());
        longPutCommand.setValue(longValue.toString());

        assertSame(longPutCommand, transformer.fromBytes(transformer.toBytes(longPutCommand)));
    }
}