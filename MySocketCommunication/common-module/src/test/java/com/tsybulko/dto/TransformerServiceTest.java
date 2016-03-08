package com.tsybulko.dto;

import com.tsybulko.dto.command.MapCommand;
import com.tsybulko.dto.command.MapCommandDTO;
import com.tsybulko.common.TestWithLogger;
import org.junit.Test;

import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

/**
 * @author Vitalii Tsybulko
 * @version 1.0
 * @since 02/29/2016 12:07
 */
public class TransformerServiceTest extends TestWithLogger {

    private TransformerService transformer = TransformerService.getInstance();

    @Test
    public void testToBytesFromBytes() throws Exception {
        HashMap<String, String> allErrors = new HashMap<String, String>();
        assertSame(MapCommand.clearAll, transformer.fromBytes(transformer.toBytes(new MapCommandDTO(MapCommand.clearAll)), allErrors).getCommand());
        assertTrue(allErrors.isEmpty());
    }

    @Test
    public void testToBytesFromBytesLongNames() throws Exception {
        MapCommandDTO longPutCommand = new MapCommandDTO(MapCommand.put);
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

        HashMap<String, String> allErrors = new HashMap<String, String>();
        MapCommandDTO commandForComparison = transformer.fromBytes(transformer.toBytes(longPutCommand), allErrors);
        assertTrue(allErrors.isEmpty());
        assertSame(longPutCommand.getCommand(), commandForComparison.getCommand());
        assertEquals(longPutCommand.getKey(), commandForComparison.getKey());
        assertEquals(longPutCommand.getValue(), commandForComparison.getValue());
    }

    @Test
    public void testTransformCorruptedBytes() throws Exception {
        HashMap<String, String> allErrors = new HashMap<String, String>();
        MapCommandDTO command = new MapCommandDTO(MapCommand.get);
        command.setKey("testKey");
        byte[] commandBytes = transformer.toBytes(command);
        commandBytes[1] += 1;
        transformer.fromBytes(commandBytes, allErrors);
        assertTrue(!allErrors.isEmpty());
    }
}
