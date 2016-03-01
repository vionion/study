package com.tsybulko.args;

import com.tsybulko.dto.MapCommand;
import org.apache.log4j.Logger;

/**
 * @author Vitalii Tsybulko
 * @version 1.0
 * @since 02/25/2016 12:05
 */
public abstract class Parser {

    private static Logger logger = Logger.getLogger(Parser.class);

    public abstract ArgsContainer parse(String[] args);

    /**
     * Simple method for achieving port number for communication and name of logfile, if exists,
     * which may be reused
     *
     * @param container container for inserting information found
     * @param args      array of command line arguments
     * @param i         number of command line argument in args which must be checked
     */
    protected void checkPortLogFile(ArgsContainer container, String[] args, int i) {
        if (container.getPort() < 0 && (args[i].equals("-port") || args[i].equals("-serverPort"))) {
            try {
                container.setPort(Integer.valueOf(args[++i]));
            } catch (NumberFormatException e) {
                logger.error("ServerPort must be an integer value.");
                System.exit(1);
            }
        } else if (args[i].equals("-logfile")) {
            container.setLogFile(args[++i]);
        }
    }

    /**
     * Initialises given command by key/key-value from successive command line arguments args according to its type
     *
     * @param command MapCommand instance which must be initialised
     * @param i       number of command line argument in args where command type was found
     * @param args    an array of command line arguments
     * @return (number of the last processed argument) + 1
     */
    protected int initCommand(MapCommand command, int i, String[] args) {
        if (command.isGet()) {
            command.setKey(args[++i]);
        } else if (command.isPut()) {
            command.setKey(args[++i]);
            command.setValue(args[++i]);
        }
        return i++;
    }

}
