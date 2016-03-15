package com.tsybulko.validate;

import com.tsybulko.args.ClientArgsContainer;
import org.apache.log4j.Logger;

import java.util.HashMap;

/**
 * @author Vitalii Tsybulko
 * @version 1.0
 * @since 03/08/2016 22:36
 */
public class ClientArgsValidator extends ArgsValidator {

    private static Logger logger = Logger.getLogger(ClientArgsValidator.class);

    /**
     * Validates arguments of ClientArgsContainer
     *
     * @param container container which must be validated
     * @param errors    map for appending errors
     * @return map with all errors
     */
    public static HashMap<String, String> validate(ClientArgsContainer container, HashMap<String, String> errors) {
        if (container.getServerHost() == null) {
            logger.error("Server host is not set correctly.");
            errors.put("serverHost", "Server host is not set correctly.");
        }
        if (container.getCommandDTO() == null) {
            logger.error("Command is not set.");
            errors.put("command", "Command is not set.");
        } else if (!container.getCommandDTO().isInitialised()) {
            logger.error("Command is not initialised correctly.");
            errors.put("command", "Command is not initialised correctly.");
        }
        ArgsValidator.validate(container, errors);
        return errors;
    }

}
