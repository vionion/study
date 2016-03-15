package com.tsybulko.integration;

import com.tsybulko.client.HegemonicClient;
import com.tsybulko.client.multithreading.ClientAggregator;
import com.tsybulko.integration.runnable.utils.RunnableClient;
import com.tsybulko.integration.runnable.utils.RunnableServer;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.HashMap;

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

    @Test
    public void testTwoClients() throws Exception {
        ClientAggregator client1 = new ClientAggregator();
        ClientAggregator client2 = new ClientAggregator();
        HashMap<String, String> errorsClient1 = client1.run(new String[]{"-serverHost", "localhost", "-serverPort", PORT, "clearAll"});
        HashMap<String, String> errorsClient2 = client2.run(new String[]{"-serverHost", "localhost", "-serverPort", PORT, "clearAll"});
        Thread.sleep(10000);
        assertTrue(errorsClient1.isEmpty());
        assertTrue(errorsClient2.isEmpty());
    }

    @Test
    public void testClientFalls() throws Exception {
        RunnableClient clientRunnable = new RunnableClient(PORT);
        Thread clientThread = new Thread(clientRunnable);
        clientThread.start();
        Thread.sleep(10000);
        clientThread.interrupt();
        assertTrue(hashMapServer.getErrors().isEmpty());
    }

    @Test
    public void testServerFalls() throws Exception {
        RunnableClient clientRunnable = new RunnableClient(PORT);
        Thread clientThread = new Thread(clientRunnable);
        clientThread.start();
        Thread.sleep(5000);
        hashMapServer.shutDown();
        Thread.sleep(3000);
        runServer(hashMapServer);
        assertTrue(clientRunnable.getErrors().isEmpty());
    }

    @AfterClass
    public static void tearDown() throws Exception {
        hashMapServer.shutDown();
    }

    public static void runServer(RunnableServer server) throws Exception {
        Thread strangeServerThread = new Thread(server);
        strangeServerThread.start();
        while (!server.isRunning()) {
            Thread.sleep(20);
        }
    }

}
