package com.tsybulko.client;

import com.tsybulko.dto.service.ResponseTransformerService;
import com.tsybulko.dto.response.ResponseDTO;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
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

    protected String serverHost;
    int port;
    private Socket clientSocket;
    protected OutputStream output;
    protected InputStream input;
    private HashMap<String, String> errors;
    private ResponseTransformerService transformer = ResponseTransformerService.getInstance();

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
            input = clientSocket.getInputStream();
        } catch (UnknownHostException e) {
            success = false;
            logger.error("Don't know about host " + serverHost);
            errors.put("clientExecution", "Don't know about host " + serverHost);
        } catch (IOException e) {
            success = false;
            logger.error("Couldn't get I/O for the connection to " +
                    serverHost + " and open connection");

            // See comment to the next function

//            errors.put("clientExecution", "Couldn't get I/O for the connection to " +
//                    serverHost + " and open connection");
        } finally {
            return success;
        }
    }

    /**
     * Sends command, which must be in byte form, to server by opened connection
     *
     * @param byteCommand byte view of command which must be sent
     * @return response object with success flag and answer or error String
     */
    public ResponseDTO sendCommand(byte[] byteCommand) {
        ResponseDTO result = new ResponseDTO(false, "Unknown problem");
        byte[] header = new byte[ResponseTransformerService.HEADER_LENGTH];
        byte[] data = new byte[0];
        int dataLength = 0;
        try {
            long start = System.nanoTime();
            output.write(byteCommand);
            output.flush();

            input.read(header, 0, header.length);
            dataLength = transformer.getDataPartSize(header);
            if (dataLength > 0) {
                data = new byte[dataLength];
                input.read(data, 0, dataLength);
            }
            result = transformer.fromBytes(header, data, errors);
            if (result == null) {
                result.setSuccess(false);
                result.setAnswer("Maybe, package is corrupted");
            }
            logger.info(result.getAnswer());
            long end = System.nanoTime();
            logger.info("Sending command had taken " + (end - start) + " nanosec.");
        } catch (IOException e) {
            result.setSuccess(false);
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
                    result = sendCommand(byteCommand);
                    break;
                }
            }
        } finally {
            return result;
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