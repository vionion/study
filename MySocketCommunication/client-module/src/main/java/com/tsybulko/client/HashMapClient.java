package com.tsybulko.client;

import com.tsybulko.args.ClientArgsContainer;
import com.tsybulko.args.ClientParser;
import com.tsybulko.dto.TransformerService;
import com.tsybulko.validate.ClientArgsValidator;
import org.apache.log4j.BasicConfigurator;
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
 * @since 02/22/2016 18:24
 */
public class HashMapClient {

    private static Logger logger = Logger.getLogger(HashMapClient.class);

    public static void main(String[] args) throws IOException {
        HashMapClient client = new HashMapClient();
        client.run(args);
    }

    /**
     * Main method, which initialises client socket, transforms command to byte form, sends it to the server and listens to the answer
     *
     * @param args arguments for setting up
     * @return exit code (exitCode > 0 if some exception was happened)
     * @throws IOException
     */
    public HashMap<String, String> run(String[] args) throws IOException {
        BasicConfigurator.configure();
        HashMap<String, String> allErrors = new HashMap<String, String>();
        ClientArgsContainer argumentContainer = ClientParser.getInstance().parse(args, allErrors);
        ClientArgsValidator.validate(argumentContainer, allErrors);
        if (allErrors.isEmpty()) {
            try {
                Socket clientSocket = new Socket(argumentContainer.getServerHost(), argumentContainer.getPort());
                OutputStream output = clientSocket.getOutputStream();
                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                byte[] fromUser = TransformerService.getInstance().toBytes(argumentContainer.getCommandDTO());
                if (fromUser != null) {
                    output.write(fromUser);
                    clientSocket.shutdownOutput();
                }
                logger.info(in.readLine());
            } catch (UnknownHostException e) {
                logger.error("Don't know about host " + argumentContainer.getServerHost());
                allErrors.put("clientExecution", "Don't know about host " + argumentContainer.getServerHost());
            } catch (IOException e) {
                logger.error("Couldn't get I/O for the connection to " +
                        argumentContainer.getServerHost());
                allErrors.put("clientExecution", "Couldn't get I/O for the connection to " +
                        argumentContainer.getServerHost());
            }
        }
        return allErrors;
    }

}