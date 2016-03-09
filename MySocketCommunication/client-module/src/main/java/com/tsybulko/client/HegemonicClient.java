package com.tsybulko.client;

import com.tsybulko.args.ClientArgsContainer;
import com.tsybulko.args.ClientParser;
import com.tsybulko.dto.TransformerService;
import com.tsybulko.validate.ClientArgsValidator;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.HashMap;

/**
 * @author Vitalii Tsybulko
 * @version 1.0
 * @since 02/22/2016 18:24
 */
public class HegemonicClient {

    private static Logger logger = Logger.getLogger(HegemonicClient.class);

    public static void main(String[] args) throws IOException {
        HegemonicClient client = new HegemonicClient();
        client.run(args);
    }

    /**
     * Main method, which parses command arguments, transforms command to byte form and with help of mignon-socket sends it to the server
     *
     * @param args arguments for setting up
     * @return HashMap of errors
     * @throws IOException
     */
    public HashMap<String, String> run(String[] args) throws IOException {
        BasicConfigurator.configure();
        HashMap<String, String> allErrors = new HashMap<String, String>();
        ClientArgsContainer argumentContainer = ClientParser.getInstance().parse(args, allErrors);
        ClientArgsValidator.validate(argumentContainer, allErrors);
        MignonClient mignon = new MignonClient(argumentContainer.getServerHost(), argumentContainer.getPort(), allErrors);
        if (allErrors.isEmpty()) {
            mignon.openConnection();
            byte[] fromUser = TransformerService.getInstance().toBytes(argumentContainer.getCommandDTO());
            if (fromUser != null) {
                mignon.sendCommand(fromUser);
            }
            mignon.closeConnection();
        }
        return allErrors;
    }

}