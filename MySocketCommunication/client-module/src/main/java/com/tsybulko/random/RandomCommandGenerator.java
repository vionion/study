package com.tsybulko.random;

import com.tsybulko.dto.command.MapCommandDTO;
import org.apache.log4j.Logger;

/**
 * @author Vitalii Tsybulko
 * @version 1.0
 * @since 03/09/2016 19:33
 */
public class RandomCommandGenerator {

    private static Logger logger = Logger.getLogger(KeyGenerator.class);

    private static KeyGenerator keyGenerator = KeyGenerator.getInstance();
    private static ValueGenerator valueGenerator = ValueGenerator.getInstance();
    private static final RandomCommandGenerator INSTANCE = new RandomCommandGenerator();

    private RandomCommandGenerator() {
    }

    public static RandomCommandGenerator getInstance() {
        return INSTANCE;
    }

    public MapCommandDTO initRandomPutCommand(MapCommandDTO command) {
        command.setKey(keyGenerator.generateKey());
        command.setValue(valueGenerator.generateValue());
        return command;
    }
}
