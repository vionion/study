package com.tsybulko.args;

import com.tsybulko.dto.MapCommand;
import org.apache.log4j.Logger;

/**
 * @author Vitalii Tsybulko
 * @version 1.0
 * @since 02/25/2016 12:05
 */
public class ClientParser extends Parser {

    private static Logger logger = Logger.getLogger(ClientParser.class);

    private static final ClientParser INSTANCE = new ClientParser();

    private ClientParser() {
    }

    public static ClientParser getInstance() {
        return INSTANCE;
    }

    public ClientArgsContainer parse(String[] args) {
        ClientArgsContainer container = new ClientArgsContainer();
        for (int i = 0; i < args.length; i++) {
            if (args[i].startsWith("-")) {
                if ((container.getServerHost() == null) && (args[i].equals("-serverHost"))) {
                    container.setServerHost(args[++i]);
                } else {
                    checkPortLogFile(container, args, i);
                }
            } else if ((container.getCommand() == null) && (args[i].equals("put") || args[i].equals("get") || args[i].equals("clearAll"))) {
                container.setCommand(MapCommand.valueOf(args[i]));
                i = initCommand(container.getCommand(), i, args);
            }
        }
        container.validate();
        return container;
    }

}
