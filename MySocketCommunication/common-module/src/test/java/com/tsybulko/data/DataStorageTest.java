package com.tsybulko.data;

import com.tsybulko.common.TestWithLogger;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author Vitalii Tsybulko
 * @version 1.0
 * @since 02/29/2016 12:55
 */
public class DataStorageTest extends TestWithLogger {

    private DataStorage storage = DataStorage.getInstance();

    @Test
    public void testGet() throws Exception {
        storage.put("key1", "val1");
        storage.put("key2", "val2");
        storage.put("key1", "val3");
        assertEquals("val3", storage.get("key1"));
        assertEquals("val2", storage.get("key2"));
    }

    @Test
    public void testPut() throws Exception {
        storage.put("key4", "val4");
        assertEquals("val4", storage.get("key4"));
    }

    @Test
    public void testReplace() throws Exception {
        storage.put("key5", "val5");
        assertEquals("val5", storage.put("key5", "val6"));
    }

    @Test
    public void testClear() throws Exception {
        storage.clear();
    }
}