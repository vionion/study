package com.tsybulko.data;

import java.util.HashMap;

/**
 * @author Vitalii Tsybulko
 * @version 1.0
 * @since 02/24/2016 20:37
 */
public class DataStorage {

    private static final DataStorage INSTANCE = new DataStorage();
    private static HashMap<String, String> data = new HashMap<String, String>();

    private DataStorage() {
    }

    public static DataStorage getInstance() {
        return INSTANCE;
    }

    public String get(String key) {
        return data.get(key);
    }

    public String put(String key, String value) {
        return data.put(key, value);
    }

    public void clear() {
        data.clear();
    }

}
