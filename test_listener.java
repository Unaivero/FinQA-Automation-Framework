package com.finqa.utils;

import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.finqa.reporting.ExtentReportManager;
import com.finqa.reporting.dashboard.DashboardGenerator;
import com.finqa.reporting.video.VideoRecorder;
import com.finqa.reporting.visual.VisualValidator;
import io.qameta.allure.Allure;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TestListener implements ITestListener {
    
    private static Map<String, ExtentTest> extentTestMap = new HashMap<>();
    private static Map<String, VideoRecorder> videoRecorderMap = new HashMap<>();
    private static List<DashboardGenerator.FailedTest> failedTests = new ArrayList<>();
    
    @Override
    public void onStart(ITestContext context) {
        System.out.println("*** Test Suite " + context.getName() + " started ***");
    }
    
    @Override
    public void onFinish(ITestContext context) {
        System.out.println("*** Test Suite " + context.getName() + " ending ***");
        
        // Generate Extent Report
        ExtentReportManager.flushReport();
        
        // Generate Dashboard
        generateDashboard(context);
    }
    
    @Override
    public void onTestStart(ITestResult result) {
        System.out.println("*** Running test method " + result.getMethod().getMethodName() + "...");
        
        // Create Extent Report test
        ExtentTest extentTest = ExtentReportManager.createTest(result.getMethod().getMethodName());
        extentTestMap.put(result.getMethod().getMethodName(), extentTest);
        
        // Start video recording if UI test
        if (isUITest(result)) {
            try {
                VideoRecorder videoRecorder = new VideoRecorder(result.getMethod().getMethodName());
                videoRecorder.startRecording();
                videoRecorderMap.put(result.getMethod().getMethodName(), videoRecorder);
            } catch (Exception e) {
                System.err.println("Error starting video recording: " + e.getMessage());
            }
        }
    }
    
    @Override
    public void onTestSuccess(ITestResult result) {
        System.out.println("*** Executed " + result.getMethod().getMethodName() + " test successfully...");
        
        // Log success in Extent Report
        ExtentTest extentTest = extentTestMap.get(result.getMethod().getMethodName());
        if (extentTest != null) {
            extentTest.log(Status.PASS, "Test passed");
        }
        
        // Create screenshot for successful visual validation
        if (isUITest(result)) {
            WebDriver driver = getDriverFromTest(result);
            if (driver != null) {
                try {
                    // Take screenshot for visual validation
                    String screenshotName = result.getMethod().getMethodName() + "_success";
                    boolean visualMatch = VisualValidator.compareWithBaseline(driver, screenshotName);
                    if (extentTest != null) {
                        extentTest.log(Status.INFO, "Visual validation: " + (visualMatch ? "Passed" : "Reference image created"));
                    }
                } catch (Exception e) {
                    System.err.println("Error during visual validation: " + e.getMessage());
                }
            }
            
            // Stop video recording
            try {
                VideoRecorder videoRecorder = videoRecorderMap.get(result.getMethod().getMethodName());
                if (videoRecorder != null) {
                    File videoFile = videoRecorder.stopRecording();
                    if (videoFile != null && videoFile.exists() && extentTest != null) {
                        extentTest.log(Status.INFO, "Test video: " + videoFile.getName());
                    }
                }
            } catch (Exception e) {
                System.err.println("Error stopping video recording: " + e.getMessage());
            }
        }
    }
    
    @Override
    public void onTestFailure(ITestResult result) {
        System.out.println("*** Test execution " + result.getMethod().getMethodName() + " failed...");
        
        // Log failure in Extent Report
        ExtentTest extentTest = extentTestMap.get(result.getMethod().getMethodName());
        if (extentTest != null) {
            extentTest.log(Status.FAIL, "Test Failed: " + result.getThrowable().getMessage());
        }
        
        // Create screenshot and video evidence for failures
        String screenshotPath = null;
        String videoPath = null;
        
        if (isUITest(result)) {
            WebDriver driver = getDriverFromTest(result);
            if (driver != null) {
                try {
                    // Take screenshot and attach to reports
                    String screenshotName = result.getMethod().getMethodName() + "_failure";
                    byte[] screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
                    
                    // Attach to Allure report
                    Allure.addAttachment(screenshotName, new ByteArrayInputStream(screenshot));
                    
                    // Save to file system
                    screenshotPath = saveScreenshotToFile(screenshot, screenshotName);
                    
                    // Attach to Extent Report
                    if (extentTest != null) {
                        extentTest.addScreenCaptureFromBase64String(
                                java.util.Base64.getEncoder().encodeToString(screenshot),
                                "Failure Screenshot");
                    }
                    
                    // Take visual comparison screenshot
                    VisualValidator.takeScreenshot(driver, screenshotName + "_visual");
                } catch (Exception e) {
                    System.err.println("Error taking failure screenshot: " + e.getMessage());
                }
            }
            
            // Stop video recording
            try {
                VideoRecorder videoRecorder = videoRecorderMap.get(result.getMethod().getMethodName());
                if (videoRecorder != null) {
                    File videoFile = videoRecorder.stopRecording();
                    if (videoFile != null && videoFile.exists()) {
                        videoPath = videoFile.getAbsolutePath();
                        if (extentTest != null) {
                            extentTest.log(Status.INFO, "Test video: " + videoFile.getName());
                        }
                    }
                }
            } catch (Exception e) {
                System.err.println("Error stopping failure video: " + e.getMessage());
            }
        }
        
        // Add to failed tests for dashboard
        try {
            String category = result.getTestClass().getName().substring(
                    result.getTestClass().getName().lastIndexOf('.') + 1);
            failedTests.add(new DashboardGenerator.FailedTest(
                    result.getMethod().getMethodName(),
                    category,
                    result.getThrowable().getMessage(),
                    screenshotPath,
                    videoPath
            ));
        } catch (Exception e) {
            System.err.println("Error adding to failed tests dashboard: " + e.getMessage());
        }
    }
    
    @Override
    public void onTestSkipped(ITestResult result) {
        System.out.println("*** Test " + result.getMethod().getMethodName() + " skipped...");
        
        // Log skip in Extent Report
        ExtentTest extentTest = extentTestMap.get(result.getMethod().getMethodName());
        if (extentTest != null) {
            String skipMessage = "Test Skipped";
            if (result.getThrowable() != null) {
                skipMessage += ": " + result.getThrowable().getMessage();
            }
            extentTest.log(Status.SKIP, skipMessage);
        }
        
        // Stop video recording if started
        if (videoRecorderMap.containsKey(result.getMethod().getMethodName())) {
            try {
                VideoRecorder videoRecorder = videoRecorderMap.get(result.getMethod().getMethodName());
                if (videoRecorder != null) {
                    videoRecorder.stopRecording();
                }
            } catch (Exception e) {
                System.err.println("Error stopping skipped test video: " + e.getMessage());
            }
        }
    }
    
    private WebDriver getDriverFromTest(ITestResult result) {
        Object testClass = result.getInstance();
        
        // No point trying if testClass is null
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
    
    private boolean isUITest(ITestResult result) {
        // First check by package convention
        if (result.getTestClass().getName().contains(".ui.")) {
            return true;
        }
        
        // Then check if the test has a WebDriver field
        return getDriverFromTest(result) != null;
    }
    
    private String saveScreenshotToFile(byte[] screenshot, String name) {
        try {
            File screenshotDir = new File("test-output/screenshots");
            if (!screenshotDir.exists()) {
                screenshotDir.mkdirs();
            }
            
            // Sanitize filename
            String safeFileName = name.replaceAll("[^a-zA-Z0-9.-]", "_");
            
            File file = new File(screenshotDir, safeFileName + ".png");
            Files.write(file.toPath(), screenshot);
            return file.getAbsolutePath();
        } catch (IOException e) {
            System.err.println("Error saving screenshot file: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
    
    private void generateDashboard(ITestContext context) {
        try {
            DashboardGenerator.TestSummary summary = new DashboardGenerator.TestSummary();
            
            // Set overall stats
            summary.setTotalTests(context.getAllTestMethods().length);
            summary.setPassed(context.getPassedTests().size());
            summary.setFailed(context.getFailedTests().size());
            summary.setSkipped(context.getSkippedTests().size());
            
            // Add categories
            Map<String, int[]> categoryCounts = new HashMap<>();
            
            // Process passed tests
            context.getPassedTests().getAllResults().forEach(result -> {
                String category = getCategoryFromTest(result);
                categoryCounts.putIfAbsent(category, new int[]{0, 0, 0});
                categoryCounts.get(category)[0]++;
            });
            
            // Process failed tests
            context.getFailedTests().getAllResults().forEach(result -> {
                String category = getCategoryFromTest(result);
                categoryCounts.putIfAbsent(category, new int[]{0, 0, 0});
                categoryCounts.get(category)[1]++;
            });
            
            // Process skipped tests
            context.getSkippedTests().getAllResults().forEach(result -> {
                String category = getCategoryFromTest(result);
                categoryCounts.putIfAbsent(category, new int[]{0, 0, 0});
                categoryCounts.get(category)[2]++;
            });
            
            // Create category objects
            for (Map.Entry<String, int[]> entry : categoryCounts.entrySet()) {
                int total = entry.getValue()[0] + entry.getValue()[1] + entry.getValue()[2];
                DashboardGenerator.TestCategory category = new DashboardGenerator.TestCategory(
                        entry.getKey(),
                        total,
                        entry.getValue()[0],
                        entry.getValue()[1],
                        entry.getValue()[2]
                );
                summary.addCategory(category);
            }
            
            // Add failed tests
            for (DashboardGenerator.FailedTest test : failedTests) {
                summary.addFailedTest(test);
            }
            
            // Generate dashboard
            DashboardGenerator.generateDashboard(summary);
        } catch (Exception e) {
            System.err.println("Error generating dashboard: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private String getCategoryFromTest(ITestResult result) {
        String fullClassName = result.getTestClass().getName();
        int lastDotIndex = fullClassName.lastIndexOf('.');
        if (lastDotIndex >= 0 && lastDotIndex < fullClassName.length() - 1) {
            return fullClassName.substring(lastDotIndex + 1);
        } else {
            return "Unknown";
        }
    }
}