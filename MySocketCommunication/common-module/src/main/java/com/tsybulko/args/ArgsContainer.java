package com.tsybulko.args;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * @author Vitalii Tsybulko
 * @version 1.0
 * @since 02/25/2016 11:53
 */
public abstract class ArgsContainer {

    private static Logger logger = Logger.getLogger(ArgsContainer.class);

    protected String logFile;

    protected int port = -1;

    public void validate() {
        boolean errors = false;
        if (port < 0) {
            logger.error("Port number can not be lower than 0.");
            errors = true;
        } else if (port < 1024 || port > 4951) {
            logger.error("Port number must be between 1024 and 4951.");
            errors = true;
        }
        if (logFile != null) {
            if (!new File(logFile).exists()) {
                logger.info("File " + logFile + " is not exists and will be created.");
            }
            try {
                Properties p = new Properties();
                p.load(new FileInputStream("log4j.properties"));
                p.put("logfilename", logFile); // overwrite "logfilename"
                PropertyConfigurator.configure(p);
            } catch (IOException e) {
                BasicConfigurator.configure();
                logger.fatal("log4j.properties is not found.");
                e.printStackTrace();
            }
        }
        if (errors) {
            printErrorMessage();
        }
    }

    protected abstract void printErrorMessage();

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
