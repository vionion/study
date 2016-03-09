package com.tsybulko.args;

import org.apache.log4j.Logger;

import java.util.HashMap;

/**
 * @author Vitalii Tsybulko
 * @version 1.0
 * @since 02/25/2016 12:05
 */
public class ServerParser extends Parser {

    private static Logger logger = Logger.getLogger(ServerParser.class);

    private static final ServerParser INSTANCE = new ServerParser();

    private ServerParser() {
    }

    public static ServerParser getInstance() {
        return INSTANCE;
    }

    public ServerArgsContainer parse(String[] args, HashMap<String, String> errors) {
        ServerArgsContainer container = new ServerArgsContainer();
        for (int i = 0; i < args.length; i++) {
            checkPortLogFile(container, args, i, errors);
        }
        return container;
    }

}
