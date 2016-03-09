package com.tsybulko.integration;

import com.tsybulko.client.HegemonicClient;
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

    public static final String PORT = "1984";
    private static RunnableServer hashMapServer;

    @BeforeClass
    public static void setUp() throws Exception {
        hashMapServer = new RunnableServer(PORT);
        runServer(hashMapServer);
    }

    @Test
    public void testPut() throws Exception {
        HegemonicClient client = new HegemonicClient();
        assertTrue(client.run(new String[]{"-serverHost", "localhost", "-serverPort", PORT, "put", "key1", "value1"}).isEmpty());
    }

    @Test
    public void testGet() throws Exception {
        HegemonicClient client = new HegemonicClient();
        assertTrue(client.run(new String[]{"-serverHost", "localhost", "-serverPort", PORT, "put", "key2", "value2"}).isEmpty());
        assertTrue(client.run(new String[]{"-serverHost", "localhost", "-serverPort", PORT, "get", "key2"}).isEmpty());
    }

    @Test
    public void testClear() throws Exception {
        HegemonicClient client = new HegemonicClient();
        assertTrue(client.run(new String[]{"-serverHost", "localhost", "-serverPort", PORT, "clearAll"}).isEmpty());
    }

    @Test
    public void testStrangePort() throws Exception {
        String strangePort = "8081";
        RunnableServer strangeHashMapServer = new RunnableServer(strangePort);
        runServer(strangeHashMapServer);

        HegemonicClient client = new HegemonicClient();
        assertTrue(client.run(new String[]{"-serverHost", "localhost", "-serverPort", strangePort, "clearAll"}).isEmpty());

        strangeHashMapServer.shutDown();
    }

    @AfterClass
    public static void tearDown() throws Exception {
        hashMapServer.shutDown();
    }

    private static void runServer(RunnableServer server) throws Exception {
        Thread strangeServerThread = new Thread(server);
        strangeServerThread.start();
        while (!server.isRunning()) {
            Thread.sleep(20);
        }
    }

    private static class RunnableServer implements Runnable {
        private HashMapServer server = new HashMapServer();
        private String port;

        public RunnableServer(String port) {
            this.port = port;
        }

        public void run() {
            try {
                server.turnOn();
                server.run(new String[]{"-port", port});
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public boolean isRunning() {
            return server.isRunning();
        }

        public void shutDown() {
            server.turnOff();
        }
    }
}
