package com.tsybulko.args;

import com.tsybulko.dto.command.MapCommandDTO;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import java.io.IOException;
import java.util.HashMap;
import java.util.Properties;

/**
 * @author Vitalii Tsybulko
 * @version 1.0
 * @since 02/25/2016 12:05
 */
public abstract class Parser {

    private static Logger logger = Logger.getLogger(Parser.class);

    public abstract ArgsContainer parse(String[] args, HashMap<String, String> errors);

    /**
     * Simple method for achieving port number for communication and name of logfile, if exists,
     * which may be reused
     *
     * @param container container for inserting information found
     * @param args      array of command line arguments
     * @param i         number of command line argument in args which must be checked
     * @param errors    HashMap for errors messages
     */
    protected void checkPortLogFile(ArgsContainer container, String[] args, int i, HashMap<String, String> errors) {
        if (container.getPort() < 0 && (args[i].equals("-port") || args[i].equals("-serverPort"))) {
            try {
                container.setPort(Integer.valueOf(args[++i]));
            } catch (NumberFormatException e) {
                logger.error("Port must be an integer value.");
                errors.put("port", "Port must be an integer value.");
            }
        } else if (args[i].equals("-logfile")) {
            container.setLogFile(args[++i]);
            if (errors.isEmpty()) {
                try {
                    Properties p = new Properties();
                    p.load(ClassLoader.getSystemClassLoader().getResourceAsStream("log4j.properties"));
                    p.put("logfilename", container.getLogFile()); // overwrite "logfilename"
                    PropertyConfigurator.configure(p);
                } catch (IOException e) {
                    BasicConfigurator.configure();
                    logger.fatal("log4j.properties is not found.", e);
                }
            }
        }
    }

    /**
     * Initialises given command by key/key-value from successive command line arguments args according to its type
     *
     * @param command MapCommandDTO instance which must be initialised
     * @param i       number of command line argument in args where command type was found
     * @param args    an array of command line arguments
     * @return (number of the last processed argument) + 1
     */
    protected int initCommand(MapCommandDTO command, int i, String[] args) {
        if (command.isGet()) {
            command.setKey(args[++i]);
        } else if (command.isPut()) {
            command.setKey(args[++i]);
            command.setValue(args[++i]);
        }
        return i++;
    }

}
