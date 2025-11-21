package com.mobile.test.config;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class ConfigManager {
    private static final String CONFIG_FILE = "src/main/resources/config.properties";
    private static final Properties properties = new Properties();
    private static ConfigManager instance;

    private ConfigManager() {
        try (FileInputStream fis = new FileInputStream(CONFIG_FILE)) {
            properties.load(fis);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load config file: " + CONFIG_FILE, e);
        }
    }

    public static synchronized ConfigManager getInstance() {
        if (instance == null) {
            instance = new ConfigManager();
        }
        return instance;
    }

    public String getProperty(String key) {
        return properties.getProperty(key);
    }

    public String getAppiumServerUrl() {
        return getProperty("appium.server.url");
    }

    public String getPlatformName() {
        return getProperty("platform.name");
    }

    public String getPlatformVersion() {
        return getProperty("platform.version");
    }

    public String getDeviceName() {
        return getProperty("device.name");
    }
}
