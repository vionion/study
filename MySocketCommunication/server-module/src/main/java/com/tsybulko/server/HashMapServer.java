package com.tsybulko.server;

import com.tsybulko.args.ServerArgsContainer;
import com.tsybulko.args.ServerParser;
import com.tsybulko.data.service.DataChangeService;
import com.tsybulko.dto.command.MapCommandDTO;
import com.tsybulko.dto.TransformerService;
import com.tsybulko.validate.ServerArgsValidator;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

/**
 * @author Vitalii Tsybulko
 * @version 1.0
 * @since 02/22/2016 18:12
 */

public class HashMapServer {

    private static Logger logger = Logger.getLogger(HashMapServer.class);
    private static volatile boolean isAlive = false;


    public static void main(String[] args) throws IOException {
        HashMapServer server = new HashMapServer();
        server.turnOn();
        server.run(args);
    }

    public HashMap<String, String> run(String[] args) throws IOException {
        BasicConfigurator.configure();
        HashMap<String, String> allErrors = new HashMap<String, String>();
        ServerArgsContainer argumentContainer = ServerParser.getInstance().parse(args, allErrors);
        ServerArgsValidator.validate(argumentContainer, allErrors);

        ServerSocket serverSocket = new ServerSocket(argumentContainer.getPort());
        OutputStream clientOut = null;
        try {
            while (isAlive && allErrors.isEmpty()) {
                Socket socket = serverSocket.accept();
                clientOut = socket.getOutputStream();
                logger.debug("hello, padawan");
                MapCommandDTO result = TransformerService.getInstance().fromBytes(IOUtils.toByteArray(socket.getInputStream()), allErrors);
                String answer = DataChangeService.getInstance().performAction(result);
                // TODO: test it!
//                throw new IOException("test error message");
                new PrintWriter(clientOut, true).println(answer == null ? "404" : answer);
                logger.info("Ok");
            }
        } catch (IOException e) {
            logger.error("Exception caught when trying to listen on port "
                    + argumentContainer.getPort() + " or listening for a connection.", e);
            if (clientOut != null) {
                new PrintWriter(clientOut, true).println("Something went wrong: " + e.getMessage());
            }
            allErrors.put("server", e.getMessage());
        } finally {
            serverSocket.close();
            return allErrors;
        }

    }

    public void turnOn() {
        isAlive = true;
    }

    public void turnOff() {
        isAlive = false;
    }
}