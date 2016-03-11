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
     *
     * @return success flag of performing operation
     */
    public boolean openConnection() {
        boolean success = true;
        try {
            clientSocket = new Socket(serverHost, port);
            clientSocket.isBound();
            output = clientSocket.getOutputStream();
            input = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        } catch (UnknownHostException e) {
            success = false;
            logger.error("Don't know about host " + serverHost);
            errors.put("clientExecution", "Don't know about host " + serverHost);
        } catch (IOException e) {
            success = false;
            logger.error("Couldn't get I/O for the connection to " +
                    serverHost + " and open connection");
            errors.put("clientExecution", "Couldn't get I/O for the connection to " +
                    serverHost + " and open connection");
        } finally {
            return success;
        }
    }

    /**
     * Sends command, which must be in byte form, to server by opened connection
     *
     * @param byteCommand byte view of command which must be sent
     * @return success flag of performing operation
     */
    public boolean sendCommand(byte[] byteCommand) {
        boolean success = true;
        try {
            long start = System.nanoTime();
            output.write(byteCommand);
            output.flush();
            logger.info(input.readLine());
            long end = System.nanoTime();
            logger.info("Sending command had taken " + (end - start) + " nanosec.");
        } catch (IOException e) {
            success = false;
            logger.error("Couldn't get I/O for the connection to " +
                    serverHost + " and send command");

            // I hide it `cause if we used errors map for interrupting application when they happens
            // we would have real problems with successive code

//            errors.put("clientExecution", "Couldn't get I/O for the connection to " +
//                    serverHost + " and send command");

            for (int i = 0; i < 10; i++) {
                Thread.sleep(1000);
                logger.info("Trying to reconnect to " + serverHost);
                if (openConnection()) {
                    success = true;
                    break;
                }
            }
        } finally {
            return success;
        }
    }

    /**
     * Just closes connection with server
     *
     * @return success flag of performing operation
     */
    public boolean closeConnection() {
        boolean success = true;
        try {
            clientSocket.shutdownOutput();
            clientSocket.close();
        } catch (IOException e) {
            success = false;
            logger.error("Couldn't get I/O for the connection to " +
                    serverHost + " and close connection");
            errors.put("clientExecution", "Couldn't get I/O for the connection to " +
                    serverHost + " and close connection");
        } finally {
            return success;
        }
    }


}