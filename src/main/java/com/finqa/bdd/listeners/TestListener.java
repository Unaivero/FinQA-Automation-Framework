package com.finqa.bdd.listeners;

import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import io.qameta.allure.Allure;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

import java.io.ByteArrayInputStream;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;

public class TestListener implements ITestListener {
    
    @Override
    public void onStart(ITestContext context) {
        System.out.println("*** Test Suite " + context.getName() + " started ***");
    }
    
    @Override
    public void onFinish(ITestContext context) {
        System.out.println("*** Test Suite " + context.getName() + " ending ***");
    }
    
    @Override
    public void onTestStart(ITestResult result) {
        System.out.println("*** Running test method " + result.getMethod().getMethodName() + "...");
    }
    
    @Override
    public void onTestSuccess(ITestResult result) {
        System.out.println("*** Executed " + result.getMethod().getMethodName() + " test successfully...");
    }
    
    @Override
    public void onTestFailure(ITestResult result) {
        System.out.println("*** Test execution " + result.getMethod().getMethodName() + " failed...");
        
        WebDriver driver = getDriverFromTest(result);
        if (driver != null) {
            try {
                // Take screenshot and attach to Allure report
                byte[] screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
                String screenshotName = result.getMethod().getMethodName() + "_failure";
                
                // Attach to Allure report
                Allure.addAttachment(screenshotName, new ByteArrayInputStream(screenshot));
                
                // Save to file system
                saveScreenshot(screenshot, screenshotName);
            } catch (Exception e) {
                System.err.println("Error taking failure screenshot: " + e.getMessage());
            }
        }
    }
    
    @Override
    public void onTestSkipped(ITestResult result) {
        System.out.println("*** Test " + result.getMethod().getMethodName() + " skipped...");
    }
    
    private WebDriver getDriverFromTest(ITestResult result) {
        Object testClass = result.getInstance();
        
        if (testClass == null) {
            return null;
        }
        
        // Try to find a WebDriver instance in the test class
        Class<?> clazz = testClass.getClass();
        while (clazz != null) {
            // Look through all fields in this class
            for (Field field : clazz.getDeclaredFields()) {
                if (WebDriver.class.isAssignableFrom(field.getType())) {
                    try {
                        field.setAccessible(true);
                        Object driverObj = field.get(testClass);
                        if (driverObj != null) {
                            return (WebDriver) driverObj;
                        }
                    } catch (Exception e) {
                        System.err.println("Error accessing WebDriver field: " + e.getMessage());
                    }
                }
            }
            // Check superclass if we didn't find it
            clazz = clazz.getSuperclass();
        }
        
        return null;
    }
    
    private void saveScreenshot(byte[] screenshot, String name) {
        try {
            Path screenshotDir = Paths.get("test-output", "screenshots");
            Files.createDirectories(screenshotDir);
            
            // Sanitize filename
            String safeFileName = name.replaceAll("[^a-zA-Z0-9.-]", "_");
            
            Path file = screenshotDir.resolve(safeFileName + ".png");
            Files.write(file, screenshot);
        } catch (Exception e) {
            System.err.println("Error saving screenshot file: " + e.getMessage());
        }
    }
}