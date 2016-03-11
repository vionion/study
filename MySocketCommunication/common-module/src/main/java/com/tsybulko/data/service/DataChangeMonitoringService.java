package com.tsybulko.data.service;

import org.apache.log4j.Logger;

/**
 * @author Vitalii Tsybulko
 * @version 1.0
 * @since 03/10/2016 11:12
 */
public class DataChangeMonitoringService {

    private static Logger logger = Logger.getLogger(DataChangeMonitoringService.class);

    private static final DataChangeMonitoringService INSTANCE = new DataChangeMonitoringService();

    private static final DataChangeMonitor monitor = new DataChangeMonitor();

    private DataChangeMonitoringService() {
    }

    public static DataChangeMonitoringService getInstance() {
        return INSTANCE;
    }


    public void runMonitor() {
        Thread monitorThread = new Thread(monitor);
        monitorThread.start();
    }

    public void stopMonitor() {
        monitor.shutDown();
    }

    public synchronized void incrementPuts() {
        monitor.incrementPuts();
    }

    private static class DataChangeMonitor implements Runnable {

        private volatile boolean running = false;

        private int putsAmount = 0;

        public void run() {
            try {
                running = true;
                while (running) {
                    Thread.sleep(1000);
                    logger.info("Load is " + putsAmount + " writes per second.");
                    annihilatePuts();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        public synchronized void incrementPuts() {
            putsAmount++;
        }

        public synchronized void annihilatePuts() {
            putsAmount = 0;
        }

        public boolean isRunning() {
            return running;
        }

        public void shutDown() {
            running = false;
        }
    }
}
