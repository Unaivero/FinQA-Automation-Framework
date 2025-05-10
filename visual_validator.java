package com.finqa.reporting.visual;

import com.github.romankh3.image.comparison.ImageComparison;
import com.github.romankh3.image.comparison.ImageComparisonUtil;
import com.github.romankh3.image.comparison.model.ComparisonResult;
import com.github.romankh3.image.comparison.model.ComparisonState;
import org.openqa.selenium.WebDriver;
import ru.yandex.qatools.ashot.AShot;
import ru.yandex.qatools.ashot.Screenshot;
import ru.yandex.qatools.ashot.shooting.ShootingStrategies;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class VisualValidator {
    
    private static final String BASELINE_FOLDER = System.getProperty("user.dir") + File.separator + "src" + File.separator + "test" + File.separator + "resources" + File.separator + "visual-baseline";
    private static final String ACTUAL_FOLDER = System.getProperty("user.dir") + File.separator + "test-output" + File.separator + "visual-results" + File.separator + "actual";
    private static final String DIFF_FOLDER = System.getProperty("user.dir") + File.separator + "test-output" + File.separator + "visual-results" + File.separator + "diff";
    
    public static void setupFolders() {
        try {
            Files.createDirectories(Paths.get(BASELINE_FOLDER));
            Files.createDirectories(Paths.get(ACTUAL_FOLDER));
            Files.createDirectories(Paths.get(DIFF_FOLDER));
        } catch (IOException e) {
            System.err.println("Error creating directories for visual validation: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    public static BufferedImage takeScreenshot(WebDriver driver, String screenshotName) {
        if (driver == null) {
            System.err.println("Cannot take screenshot: WebDriver is null");
            return null;
        }
        
        setupFolders();
        
        try {
            Screenshot screenshot = new AShot()
                    .shootingStrategy(ShootingStrategies.viewportPasting(1000))
                    .takeScreenshot(driver);
            
            BufferedImage image = screenshot.getImage();
            
            if (image == null) {
                System.err.println("Failed to capture screenshot: Image is null");
                return null;
            }
            
            String safeName = screenshotName.replaceAll("[^a-zA-Z0-9.-]", "_");
            File file = new File(ACTUAL_FOLDER + File.separator + safeName + ".png");
            ImageIO.write(image, "PNG", file);
            System.out.println("Screenshot saved at: " + file.getAbsolutePath());
            
            return image;
        } catch (IOException e) {
            System.err.println("Error saving screenshot: " + e.getMessage());
            e.printStackTrace();
            return null;
        } catch (Exception e) {
            System.err.println("Unexpected error taking screenshot: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
    
    public static boolean compareWithBaseline(WebDriver driver, String screenshotName) {
        if (driver == null) {
            System.err.println("Cannot compare with baseline: WebDriver is null");
            return false;
        }
        
        setupFolders();
        
        // Take current screenshot
        BufferedImage actualImage = takeScreenshot(driver, screenshotName);
        if (actualImage == null) {
            return false;
        }
        
        // Check if baseline exists
        String safeName = screenshotName.replaceAll("[^a-zA-Z0-9.-]", "_");
        File baselineFile = new File(BASELINE_FOLDER + File.separator + safeName + ".png");
        
        // If baseline doesn't exist, save current as baseline and return true
        if (!baselineFile.exists()) {
            try {
                ImageIO.write(actualImage, "PNG", baselineFile);
                System.out.println("Baseline created for: " + screenshotName);
                return true;
            } catch (IOException e) {
                System.err.println("Error creating baseline: " + e.getMessage());
                e.printStackTrace();
                return false;
            }
        }
        
        // Compare with existing baseline
        try {
            BufferedImage expectedImage = ImageComparisonUtil.readImageFromFile(baselineFile);
            
            // Verify image dimensions match or resize if needed
            if (expectedImage.getWidth() != actualImage.getWidth() || 
                expectedImage.getHeight() != actualImage.getHeight()) {
                System.out.println("Warning: Image dimensions don't match. Resizing for comparison.");
                // In a real implementation, you might want to resize one of the images
            }
            
            ImageComparison imageComparison = new ImageComparison(expectedImage, actualImage);
            imageComparison.setDrawExcludedRectangles(true);
            
            // Set threshold for difference (1-100)
            imageComparison.setThreshold(5);
            
            ComparisonResult comparisonResult = imageComparison.compareImages();
            
            // Save diff image
            File diffFile = new File(DIFF_FOLDER + File.separator + safeName + "_diff.png");
            ImageIO.write(comparisonResult.getResult(), "PNG", diffFile);
            
            boolean matches = comparisonResult.getComparisonState() == ComparisonState.MATCH;
            if (!matches) {
                System.out.println("Visual difference detected. Diff saved at: " + diffFile.getAbsolutePath());
            }
            
            return matches;
            
        } catch (IOException e) {
            System.err.println("Error comparing images: " + e.getMessage());
            e.printStackTrace();
            return false;
        } catch (Exception e) {
            System.err.println("Unexpected error in visual comparison: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}