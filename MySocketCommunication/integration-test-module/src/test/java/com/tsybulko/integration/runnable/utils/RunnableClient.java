package com.tsybulko.integration.runnable.utils;

import com.tsybulko.client.multithreading.ClientAggregator;

import java.io.IOException;
import java.util.HashMap;

/**
 * @author Vitalii Tsybulko
 * @version 1.0
 * @since 03/14/2016 16:45
 */
public class RunnableClient implements Runnable {
    private ClientAggregator client = new ClientAggregator();
    private HashMap<String, String> errorsClient;
    private String port;

    public RunnableClient(String port) {
        this.port = port;
    }

    public void run() {
        try {
            errorsClient = client.run(new String[]{"-serverHost", "localhost", "-serverPort", port, "clearAll"});
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public HashMap<String, String> getErrors() {
        return errorsClient;
    }
}