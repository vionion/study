package com.tsybulko.random;

import org.apache.log4j.Logger;

import java.util.Random;

/**
 * @author Vitalii Tsybulko
 * @version 1.0
 * @since 03/09/2016 19:17
 */
public class ValueGenerator {

    private static Logger logger = Logger.getLogger(ValueGenerator.class);
    private static final Random rand = new Random();
    private static final ValueGenerator INSTANCE = new ValueGenerator();

    private ValueGenerator() {
    }

    public static ValueGenerator getInstance() {
        return INSTANCE;
    }

    public String generateValue() {
        return "value" + rand.nextInt();
    }
}
