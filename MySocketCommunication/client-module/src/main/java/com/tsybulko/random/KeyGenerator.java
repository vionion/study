package com.tsybulko.random;

import org.apache.log4j.Logger;

/**
 * @author Vitalii Tsybulko
 * @version 1.0
 * @since 03/09/2016 19:17
 */
public class KeyGenerator {

    private static final int DIVERSITY = 100000;
    private static Logger logger = Logger.getLogger(KeyGenerator.class);

    private static final KeyGenerator INSTANCE = new KeyGenerator();

    private KeyGenerator() {
    }

    public static KeyGenerator getInstance() {
        return INSTANCE;
    }

    public String generateKey() {
        return "key" + (int) (Math.random() * DIVERSITY);
    }
}
