package com.tsybulko.client.multithreading;

import com.tsybulko.args.ClientArgsContainer;
import com.tsybulko.args.ClientParser;
import com.tsybulko.validate.ClientArgsValidator;
import org.apache.log4j.BasicConfigurator;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;

/**
 * @author Vitalii Tsybulko
 * @version 1.0
 * @since 03/09/2016 19:39
 */
public class ClientAggregator {

    private LinkedList<RunnableMignon> minions = new LinkedList<RunnableMignon>();
    private final static int MINIONS_AMOUNT = 100;


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
        RunnableMignon mignon;
        for (int i = 0; i < MINIONS_AMOUNT; i++) {
            mignon = new RunnableMignon(argumentContainer.getServerHost(), argumentContainer.getPort());
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
        ClientAggregator aggregator = new ClientAggregator();
        aggregator.attachShutDownHook();
        aggregator.run(args);
    }

}
