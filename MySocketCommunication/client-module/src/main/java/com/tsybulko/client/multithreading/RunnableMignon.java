package com.tsybulko.client.multithreading;

import com.tsybulko.client.MignonClient;
import com.tsybulko.dto.TransformerService;
import com.tsybulko.dto.command.MapCommand;
import com.tsybulko.dto.command.MapCommandDTO;
import com.tsybulko.random.RandomCommandGenerator;
import org.apache.log4j.Logger;

/**
 * @author Vitalii Tsybulko
 * @version 1.0
 * @since 03/09/2016 19:25
 */
public class RunnableMignon implements Runnable {

    private static Logger logger = Logger.getLogger(RunnableMignon.class);

    private MignonClient mignon;
    private volatile boolean isRunning = false;
    private static RandomCommandGenerator commandGenerator = RandomCommandGenerator.getInstance();
    private static TransformerService transformerService = TransformerService.getInstance();


    public RunnableMignon(String host, int port) {
        mignon = new MignonClient(host, port);
    }

    /**
     * Simple Runnable implementation, which uses MignonClient for sending random commands
     * until terminate command or loosing connection with server
     */
    public void run() {
        isRunning = true;
        mignon.openConnection();
        MapCommandDTO command = new MapCommandDTO(MapCommand.put);
        while (isRunning) {
            commandGenerator.initRandomPutCommand(command);
            isRunning = mignon.sendCommand(transformerService.toBytes(command));
        }
        mignon.closeConnection();
    }

    public void shutDown() {
        isRunning = false;
    }
}
