package com.tsybulko.integration;

import com.tsybulko.client.HashMapClient;
import com.tsybulko.server.HashMapServer;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertTrue;

/**
 * @author Vitalii Tsybulko
 * @version 1.0
 * @since 02/29/2016 15:08
 */
public class CommunicationIT {

    private static Thread serverThread;
    private static HashMapServer hashMapServer;

    @BeforeClass
    public static void setUp() {
        Runnable runnable = new Runnable() {
            public void run() {
                try {
                    hashMapServer = new HashMapServer();
                    hashMapServer.turnOn();
                    hashMapServer.run(new String[]{"-port", "1984"});
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
        serverThread = new Thread(runnable);
        serverThread.start();
    }

    @Test
    public void testPut() throws Exception {
        HashMapClient client = new HashMapClient();
        assertTrue(client.run(new String[]{"-serverHost", "localhost", "-serverPort", "1984", "put", "key1", "value1"}).isEmpty());
    }

    @Test
    public void testGet() throws Exception {
        HashMapClient client = new HashMapClient();
        assertTrue(client.run(new String[]{"-serverHost", "localhost", "-serverPort", "1984", "put", "key2", "value2"}).isEmpty());
        assertTrue(client.run(new String[]{"-serverHost", "localhost", "-serverPort", "1984", "get", "key2"}).isEmpty());
    }

    @Test
    public void testClear() throws Exception {
        HashMapClient client = new HashMapClient();
        assertTrue(client.run(new String[]{"-serverHost", "localhost", "-serverPort", "1984", "clearAll"}).isEmpty());
    }

    @AfterClass
    public static void tearDown() throws Exception {
        hashMapServer.turnOff();
    }
}
