package com.finqa.config;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.safari.SafariDriver;

import java.time.Duration;

public class WebDriverConfig {

    private static WebDriver driver;
    
    public static WebDriver initializeDriver(String browser) {
        try {
            switch (browser.toLowerCase()) {
                case "chrome":
                    WebDriverManager.chromedriver().setup();
                    ChromeOptions chromeOptions = new ChromeOptions();
                    chromeOptions.addArguments("--start-maximized");
                    chromeOptions.addArguments("--disable-notifications");
                    
                    // Para capturas de pantalla más confiables
                    chromeOptions.addArguments("--disable-popup-blocking");
                    chromeOptions.addArguments("--disable-extensions");
                    
                    // Optimizaciones de rendimiento
                    chromeOptions.addArguments("--disable-gpu");
                    chromeOptions.addArguments("--no-sandbox");
                    chromeOptions.addArguments("--disable-dev-shm-usage");
                    
                    // Para entornos CI/CD
                    if (isRunningInCI()) {
                        chromeOptions.addArguments("--headless");
                    }
                    
                    driver = new ChromeDriver(chromeOptions);
                    break;
                case "firefox":
                    WebDriverManager.firefoxdriver().setup();
                    FirefoxOptions firefoxOptions = new FirefoxOptions();
                    firefoxOptions.addArguments("-width=1920");
                    firefoxOptions.addArguments("-height=1080");
                    
                    // Para entornos CI/CD
                    if (isRunningInCI()) {
                        firefoxOptions.addArguments("-headless");
                    }
                    
                    driver = new FirefoxDriver(firefoxOptions);
                    break;
                case "edge":
                    WebDriverManager.edgedriver().setup();
                    driver = new EdgeDriver();
                    break;
                case "safari":
                    driver = new SafariDriver();
                    break;
                default:
                    System.err.println("Browser " + browser + " is not supported. Defaulting to Chrome.");
                    WebDriverManager.chromedriver().setup();
                    ChromeOptions defaultOptions = new ChromeOptions();
                    defaultOptions.addArguments("--start-maximized");
                    
                    if (isRunningInCI()) {
                        defaultOptions.addArguments("--headless");
                    }
                    
                    driver = new ChromeDriver(defaultOptions);
            }
            
            // Configure timeouts safely
            try {
                driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
                driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(30));
                driver.manage().timeouts().scriptTimeout(Duration.ofSeconds(30));
                
                // Maximizar ventana para capturas de pantalla completas
                driver.manage().window().maximize();
            } catch (Exception e) {
                System.err.println("Warning: Error setting WebDriver timeouts: " + e.getMessage());
            }
            
            return driver;
        } catch (Exception e) {
            System.err.println("Error initializing WebDriver: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to initialize WebDriver: " + e.getMessage(), e);
        }
    }
    
    public static void quitDriver() {
        if (driver != null) {
            try {
                driver.quit();
            } catch (Exception e) {
                System.err.println("Error quitting WebDriver: " + e.getMessage());
            } finally {
                driver = null;
            }
        }
    }
    
    /**
     * Detecta si estamos ejecutando en un entorno CI/CD
     */
    private static boolean isRunningInCI() {
        return System.getenv("CI") != null || System.getenv("GITHUB_ACTIONS") != null;
    }
}