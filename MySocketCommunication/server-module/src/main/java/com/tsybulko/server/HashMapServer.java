package com.tsybulko.server;

import com.tsybulko.args.ServerArgsContainer;
import com.tsybulko.args.ServerParser;
import com.tsybulko.data.service.DataChangeService;
import com.tsybulko.dto.MapCommand;
import com.tsybulko.dto.service.TransformerService;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * @author Vitalii Tsybulko
 * @version 1.0
 * @since 02/22/2016 18:12
 */

public class HashMapServer {

    private static Logger logger = Logger.getLogger(HashMapServer.class);
    private static volatile boolean isAlive = false;


    public static void main(String[] args) throws IOException {
        turnOn();
        System.exit(run(args));
    }

    /**
     * Runs the client.
     */
    public static int run(String[] args) throws IOException {
        int exitCode = 0;
        BasicConfigurator.configure();
        ServerArgsContainer argumentContainer = ServerParser.getInstance().parse(args);

        ServerSocket listener = new ServerSocket(argumentContainer.getPort());
        OutputStream clientOut = null;
        try {
            while (isAlive) {
                Socket socket = listener.accept();
                clientOut = socket.getOutputStream();
                logger.debug("hello, padawan");
                MapCommand result = TransformerService.getInstance().fromBytes(IOUtils.toByteArray(socket.getInputStream()));
                DataChangeService.getInstance().performAction(result);
                new PrintWriter(clientOut, true).println("Ok");
                logger.info("Ok");
//                throw new IOException("test error message");
            }
        } catch (IOException e) {
            logger.error("Exception caught when trying to listen on port "
                    + argumentContainer.getPort() + " or listening for a connection.");
            e.printStackTrace();
            if (clientOut != null) {
                new PrintWriter(clientOut, true).println("Something went wrong: " + e.getMessage());
            }
            exitCode = 1;
        } finally {
            listener.close();
            return exitCode;
        }

    }

    public static void turnOn() {
        isAlive = true;
    }

    public static void turnOff() {
        isAlive = false;
    }
}