package com.tsybulko.integration.runnable.utils;

import com.tsybulko.server.HashMapServer;

import java.io.IOException;
import java.util.HashMap;

/**
 * @author Vitalii Tsybulko
 * @version 1.0
 * @since 03/14/2016 16:44
 */
public class RunnableServer implements Runnable {
    private HashMapServer server = new HashMapServer();
    private HashMap<String, String> errorsServer;
    private String port;

    public RunnableServer(String port) {
        this.port = port;
    }

    public void run() {
        try {
            errorsServer = server.run(new String[]{"-port", port});
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

    public HashMap<String, String> getErrors() {
        return errorsServer;
    }
}
