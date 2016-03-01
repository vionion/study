package com.tsybulko.data.service;

import com.tsybulko.data.DataStorage;
import com.tsybulko.dto.MapCommand;
import org.apache.log4j.Logger;

/**
 * @author Vitalii Tsybulko
 * @version 1.0
 * @since 02/24/2016 20:37
 */
public class DataChangeService {

    private static Logger logger = Logger.getLogger(DataChangeService.class);

    private DataStorage dataStorage = DataStorage.getInstance();

    private static final DataChangeService INSTANCE = new DataChangeService();

    private DataChangeService() {
    }

    public static DataChangeService getInstance() {
        return INSTANCE;
    }

    public void performAction(MapCommand command) {
        if (command.isClear()) {
            dataStorage.clear();
            logger.info("All data is successfully erased.");
        } else if (command.isGet()) {
            logger.info("Value from HashMap data storage by key \"" + command.getKey() + "\" is \"" + dataStorage.get(command.getKey()) + "\".");
        } else if (command.isPut()) {
            logger.info("Successfully " + (dataStorage.put(command.getKey(), command.getValue()) == null ? "inserted." : "replaced."));
        } else {
            logger.error("Unknown command.");
        }
    }

}
