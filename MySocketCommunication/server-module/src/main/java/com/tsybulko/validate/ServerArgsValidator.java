package com.tsybulko.validate;

import com.tsybulko.args.ServerArgsContainer;
import org.apache.log4j.Logger;

import java.util.HashMap;

/**
 * @author Vitalii Tsybulko
 * @version 1.0
 * @since 03/08/2016 22:36
 */
public class ServerArgsValidator extends ArgsValidator{

    private static Logger logger = Logger.getLogger(ServerArgsValidator.class);

    public static HashMap<String, String> validate(ServerArgsContainer container, HashMap<String, String> errors) {
        ArgsValidator.validate(container, errors);
        return errors;
    }

    protected static void printErrorMessage(HashMap<String, String> errors) {
        logger.error("Usage: java -jar cache-server.jar <-port PORT> [-logfile filename.log]");
        ArgsValidator.printErrorMessage(errors);
    }

}
