package com.tsybulko.client;

import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.HashMap;

/**
 * @author Vitalii Tsybulko
 * @version 1.0
 * @since 03/08/2016 23:53
 */
public class MignonClient {

    private static Logger logger = Logger.getLogger(MignonClient.class);

    String serverHost;
    int port;
    Socket clientSocket;
    OutputStream output;
    BufferedReader input;
    HashMap<String, String> errors;

    public MignonClient(String serverHost, int port) {
        this.serverHost = serverHost;
        this.port = port;
        errors = new HashMap<String, String>();
    }

    public MignonClient(String serverHost, int port, HashMap<String, String> errors) {
        this.serverHost = serverHost;
        this.port = port;
        this.errors = errors;
    }

    /**
     * Opens connection with server socket, based on given in constructor params
     */
    public void openConnection() {
        try {
            clientSocket = new Socket(serverHost, port);
            clientSocket.isBound();
            output = clientSocket.getOutputStream();
            input = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        } catch (UnknownHostException e) {
            logger.error("Don't know about host " + serverHost);
            errors.put("clientExecution", "Don't know about host " + serverHost);
        } catch (IOException e) {
            logger.error("Couldn't get I/O for the connection to " +
                    serverHost);
            errors.put("clientExecution", "Couldn't get I/O for the connection to " +
                    serverHost);
        }
    }

    /**
     * Sends command, which must be in byte form, to server by opened connection
     *
     * @param fromUser byte view of command which must be sent
     */
    public void sendCommand(byte[] fromUser) {
        try {
            long start = System.currentTimeMillis();
            output.write(fromUser);
            output.flush();
            logger.info(input.readLine());
            long end = System.currentTimeMillis();
            logger.info("Sending command had taken " + (end - start) + " millis");
        } catch (IOException e) {
            logger.error("Couldn't get I/O for the connection to " +
                    serverHost);
            errors.put("clientExecution", "Couldn't get I/O for the connection to " +
                    serverHost);
        }
    }

    /**
     * Just closes connection with server
     */
    public void closeConnection() {
        try {
            clientSocket.shutdownOutput();
        } catch (IOException e) {
            logger.error("Couldn't get I/O for the connection to " +
                    serverHost);
            errors.put("clientExecution", "Couldn't get I/O for the connection to " +
                    serverHost);
        }
    }
}