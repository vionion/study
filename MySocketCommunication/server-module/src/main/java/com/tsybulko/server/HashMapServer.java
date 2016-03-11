package com.tsybulko.server;

import com.tsybulko.args.ServerArgsContainer;
import com.tsybulko.args.ServerParser;
import com.tsybulko.validate.ServerArgsValidator;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;

import java.io.IOException;
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
    private volatile boolean enabled = false;
    private volatile boolean running = false;


    public static void main(String[] args) throws IOException {
        HashMapServer server = new HashMapServer();
        server.turnOn();
        server.run(args);
    }

    /**
     * Turns on server and manage creating new threads for processing new clientSockets connections
     * @param args arguments for setting up
     * @return HashMap of errors
     * @throws IOException
     */
    public HashMap<String, String> run(String[] args) throws IOException {
        BasicConfigurator.configure();
        HashMap<String, String> allErrors = new HashMap<String, String>();
        ServerArgsContainer argumentContainer = ServerParser.getInstance().parse(args, allErrors);
        ServerArgsValidator.validate(argumentContainer, allErrors);
        ServerSocket serverSocket = new ServerSocket(argumentContainer.getPort());
        running = true;
        Socket socket;
        //Starts new thread for every client, connected to this server
        while (enabled && allErrors.isEmpty()) {
            socket = serverSocket.accept();
            ClientProcessorThread clientProcessorThread = new ClientProcessorThread(socket, allErrors);
            clientProcessorThread.start();
        }
        running = false;
        serverSocket.close();
        return allErrors;

    }

    public void turnOn() {
        enabled = true;
    }

    public void turnOff() {
        enabled = false;
    }

    public boolean isRunning() {
        return running;
    }
}