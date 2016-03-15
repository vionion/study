package com.tsybulko.client.multithreading;

import com.tsybulko.args.ClientArgsContainer;
import com.tsybulko.args.ClientParser;
import com.tsybulko.validate.ClientArgsValidator;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;

/**
 * @author Vitalii Tsybulko
 * @version 1.0
 * @since 03/09/2016 19:39
 */
public class ClientAggregator {

    private static Logger logger = Logger.getLogger(ClientAggregator.class);

    private LinkedList<RunnableMignon> minions = new LinkedList<RunnableMignon>();
    private static int minionsAmount = 100;

    public ClientAggregator(int minionsAmount) {
        this.minionsAmount = minionsAmount;
    }

    public ClientAggregator() {
    }


    /**
     * Works very similar to HegemonicClient, but uses more than one mignon
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
        if (!allErrors.isEmpty()) {
            logger.error("Usage: java -jar cache-client.jar <-serverHost HOST> <-serverPort PORT> <put KEY VALUE | get KEY | clearAll> [-logfile filename.log]");
            allErrors.put("usage", "Usage: java -jar cache-client.jar <-serverHost HOST> <-serverPort PORT> <put KEY VALUE | get KEY | clearAll> [-logfile filename.log]");
        }
        RunnableMignon mignon;
        for (int i = 0; i < minionsAmount; i++) {
            mignon = new RunnableMignon(argumentContainer.getServerHost(), argumentContainer.getPort(), allErrors);
            minions.add(mignon);
            new Thread(mignon).start();
        }
        return allErrors;
    }

    /**
     * After terminating turns off all minions
     */
    private void attachShutDownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                for (RunnableMignon mignon : minions) {
                    mignon.shutDown();
                }
            }
        });
    }

    public static void main(String[] args) throws Exception {
        ClientAggregator aggregator = new ClientAggregator(100);
        aggregator.attachShutDownHook();
        aggregator.run(args);
    }

}
