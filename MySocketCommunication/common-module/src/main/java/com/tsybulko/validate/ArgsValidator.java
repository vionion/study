package com.tsybulko.validate;

import com.tsybulko.args.ArgsContainer;
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
public abstract class ArgsValidator {

    private static Logger logger = Logger.getLogger(ArgsValidator.class);

    /**
     * Validates ArgsContainer arguments
     * @param container container which must be validated
     * @param errors map for appending errors
     * @return map with all errors
     */
    protected static HashMap<String, String> validate(ArgsContainer container, HashMap<String, String> errors) {
        if (container.getPort() < 0) {
            logger.error("Port number can not be lower than 0.");
            errors.put("port", "Port number can not be lower than 0.");
        } else if (container.getPort() < 1024) {
            logger.error("Port number must be greater than 1024.");
            errors.put("port", "Port number must be greater than 1024.");
        }
        if (container.getLogFile() != null) {
            if (!new File(container.getLogFile()).exists()) {
                logger.info("File " + container.getLogFile() + " is not exists and will be created.");
            }
            try {
                Properties p = new Properties();
                p.load(new FileInputStream("log4j.properties"));
                p.put("logfilename", container.getLogFile()); // overwrite "logfilename"
                PropertyConfigurator.configure(p);
            } catch (IOException e) {
                BasicConfigurator.configure();
                logger.fatal("log4j.properties is not found.", e);
            }
        }
        if (!errors.isEmpty()) {
            printErrorMessage(errors);
        }
        return errors;
    }

    protected static void printErrorMessage(HashMap<String, String> errors){}

}
