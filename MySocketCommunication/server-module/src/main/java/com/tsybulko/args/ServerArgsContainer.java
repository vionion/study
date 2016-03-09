package com.tsybulko.args;

import org.apache.log4j.Logger;

/**
 * @author Vitalii Tsybulko
 * @version 1.0
 * @since 02/25/2016 11:57
 */
public class ServerArgsContainer extends ArgsContainer {

    private static Logger logger = Logger.getLogger(ServerArgsContainer.class);

    public ServerArgsContainer() {
        init();
    }

}
