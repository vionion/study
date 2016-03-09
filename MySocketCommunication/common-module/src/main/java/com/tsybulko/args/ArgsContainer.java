package com.tsybulko.args;

import org.apache.log4j.Logger;

/**
 * @author Vitalii Tsybulko
 * @version 1.0
 * @since 02/25/2016 11:53
 */
public abstract class ArgsContainer {

    private static Logger logger = Logger.getLogger(ArgsContainer.class);

    protected String logFile;

    protected int port;

    protected void init() {
        port = -1;
    }

    public String getLogFile() {
        return logFile;
    }

    public void setLogFile(String logFile) {
        this.logFile = logFile;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }
}
