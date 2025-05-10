package com.finqa.bdd.context;

import com.finqa.config.ConfigManager;
import com.finqa.config.WebDriverConfig;
import org.openqa.selenium.WebDriver;

import java.util.HashMap;
import java.util.Map;

public class TestContext {
    
    private WebDriver driver;
    private Map<String, Object> scenarioContext;
    
    public TestContext() {
        scenarioContext = new HashMap<>();
    }
    
    public WebDriver getDriver() {
        if (driver == null) {
            driver = WebDriverConfig.initializeDriver(ConfigManager.getBrowser());
        }
        return driver;
    }
    
    public void quitDriver() {
        if (driver != null) {
            driver.quit();
            driver = null;
        }
    }
    
    public void setContextValue(String key, Object value) {
        scenarioContext.put(key, value);
    }
    
    public Object getContextValue(String key) {
        return scenarioContext.get(key);
    }
    
    public Boolean isContains(String key) {
        return scenarioContext.containsKey(key);
    }
}