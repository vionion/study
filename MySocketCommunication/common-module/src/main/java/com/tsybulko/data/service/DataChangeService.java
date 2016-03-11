package com.tsybulko.data.service;

import com.tsybulko.data.DataStorage;
import com.tsybulko.dto.command.MapCommandDTO;
import org.apache.log4j.Logger;

/**
 * @author Vitalii Tsybulko
 * @version 1.0
 * @since 02/24/2016 20:37
 */
public class DataChangeService {

    private static Logger logger = Logger.getLogger(DataChangeService.class);

    private DataStorage dataStorage = DataStorage.getInstance();
    private DataChangeMonitoringService monitoringService = DataChangeMonitoringService.getInstance();

    private static final DataChangeService INSTANCE = new DataChangeService();

    private DataChangeService() {
        monitoringService.runMonitor();
    }

    public static DataChangeService getInstance() {
        return INSTANCE;
    }

    public String performAction(MapCommandDTO command) {
        String result = null;
        if (command.isClear()) {
            dataStorage.clear();
            logger.info("All data is successfully erased.");
            result = "Data erased.";
        } else if (command.isGet()) {
            result = dataStorage.get(command.getKey());
            logger.info("Value from HashMap data storage by key \"" + command.getKey() + "\" is \"" + result + "\".");
        } else if (command.isPut()) {
            result = dataStorage.put(command.getKey(), command.getValue());
            monitoringService.incrementPuts();
            logger.info("Successfully " + (result == null ? "inserted." : "replaced."));
        } else {
            logger.error("Unknown command.");
            result = "Unknown command.";
        }
        return result;
    }

}
