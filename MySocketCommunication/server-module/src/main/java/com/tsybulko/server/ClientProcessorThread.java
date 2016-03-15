package com.tsybulko.server;

import com.tsybulko.data.service.DataChangeService;
import com.tsybulko.dto.service.MapCommandTransformerService;
import com.tsybulko.dto.service.ResponseTransformerService;
import com.tsybulko.dto.command.MapCommandDTO;
import com.tsybulko.dto.response.ResponseDTO;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.HashMap;

/**
 * @author Vitalii Tsybulko
 * @version 1.0
 * @since 03/09/2016 14:05
 */
public class ClientProcessorThread extends Thread {

    private static Logger logger = Logger.getLogger(ClientProcessorThread.class);
    private final HashMap<String, String> errors;
    private static final MapCommandTransformerService mapTransformer = MapCommandTransformerService.getInstance();
    private static final ResponseTransformerService responseTransformer = ResponseTransformerService.getInstance();
    protected Socket socket;

    public ClientProcessorThread(Socket clientSocket, HashMap<String, String> allErrors) {
        this.socket = clientSocket;
        this.errors = allErrors;
    }

    /**
     * Starts thread for processing communication with one of clients
     */
    @Override
    public void run() {
        InputStream clientIn;
        OutputStream clientOut = null;
        byte[] header = new byte[MapCommandTransformerService.HEADER_LENGTH];
        byte[] data = new byte[0];
        int dataLength;
        try {
            clientOut = socket.getOutputStream();
            clientIn = socket.getInputStream();
            logger.debug("hello, padawan");
            while (true) {
                long start = System.nanoTime();
                if (clientIn.read(header, 0, header.length) < 0) {
                    break;
                }
                dataLength = mapTransformer.getDataPartSize(header);
                if (dataLength > 0) {
                    data = new byte[dataLength];
                    clientIn.read(data, 0, dataLength);
                }
                MapCommandDTO command = mapTransformer.fromBytes(header, data, errors);
                if (command != null) {
                    ResponseDTO response = DataChangeService.getInstance().performAction(command);
                    // TODO: test it!
//                throw new IOException("test error message");
                    clientOut.write(responseTransformer.toBytes(response));
                    clientOut.flush();

                    logger.info("Ok");
                    long end = System.nanoTime();
                    logger.info("Processing command had taken " + (end - start) + " nanosec.");
                }
            }
            clientIn.close();
            clientOut.close();
        } catch (IOException e) {
            logger.error("Exception caught when trying to listen on port "
                    + socket.getPort() + " or listening for a connection.", e);
            errors.put("server", e.getMessage());
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}