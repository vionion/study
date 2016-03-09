package com.tsybulko.args;

import com.tsybulko.dto.command.MapCommandDTO;
import org.apache.log4j.Logger;

/**
 * @author Vitalii Tsybulko
 * @version 1.0
 * @since 02/25/2016 11:57
 */
public class ClientArgsContainer extends ArgsContainer {

    private static Logger logger = Logger.getLogger(ClientArgsContainer.class);

    protected String serverHost;
    protected MapCommandDTO command;

    public ClientArgsContainer() {
        init();
    }

    @Override
    protected void init() {
        super.init();
        serverHost = null;
        command = null;
    }

    public String getServerHost() {
        return serverHost;
    }

    public void setServerHost(String serverHost) {
        this.serverHost = serverHost;
    }

    public MapCommandDTO getCommandDTO() {
        return command;
    }

    public void setCommandDTO(MapCommandDTO command) {
        this.command = command;
    }
}
