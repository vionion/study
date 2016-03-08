package com.tsybulko.validate;

import com.tsybulko.args.ArgsContainer;
import com.tsybulko.args.ClientArgsContainer;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Properties;

/**
 * @author Vitalii Tsybulko
 * @version 1.0
 * @since 03/08/2016 22:36
 */
public class ClientArgsValidator extends ArgsValidator{

    private static Logger logger = Logger.getLogger(ClientArgsValidator.class);

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

    protected static void printErrorMessage(HashMap<String, String> errors) {
        logger.error("Usage: java -jar cache-client.jar <-serverHost HOST> <-serverPort PORT> <put KEY VALUE | get KEY | clearAll> [-logfile filename.log]");
        ArgsValidator.printErrorMessage(errors);
    }

}
