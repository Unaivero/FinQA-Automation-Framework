package com.finqa.config;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class ConfigManager {
    
    private static Properties properties;
    private static final String CONFIG_FILE = "src/main/resources/config.properties";
    
    static {
        try {
            properties = new Properties();
            FileInputStream fis = new FileInputStream(CONFIG_FILE);
            properties.load(fis);
            fis.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public static String getProperty(String key) {
        return properties.getProperty(key);
    }
    
    public static String getBrowser() {
        return getProperty("browser");
    }
    
    public static String getBaseUrl() {
        return getProperty("base.url");
    }
    
    public static String getApiBaseUrl() {
        return getProperty("api.base.url");
    }
}