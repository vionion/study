package com.tsybulko.args;

import com.tsybulko.dto.MapCommand;
import org.apache.log4j.Logger;

/**
 * @author Vitalii Tsybulko
 * @version 1.0
 * @since 02/25/2016 11:57
 */
public class ClientArgsContainer extends ArgsContainer {

    private static Logger logger = Logger.getLogger(ClientArgsContainer.class);

    protected String serverHost = null;
    protected MapCommand command = null;

    @Override
    public void validate() {
        super.validate();
        if (serverHost == null || command == null || !command.isInitialised()) {
            printErrorMessage();
        }
    }

    protected void printErrorMessage() {
        logger.error("Usage: java -jar cache-client.jar <-serverHost HOST> <-serverPort PORT> <put KEY VALUE | get KEY | clearAll> [-logfile filename.log]");
        System.exit(1);
    }

    public String getServerHost() {
        return serverHost;
    }

    public void setServerHost(String serverHost) {
        this.serverHost = serverHost;
    }

    public MapCommand getCommand() {
        return command;
    }

    public void setCommand(MapCommand command) {
        this.command = command;
    }
}
