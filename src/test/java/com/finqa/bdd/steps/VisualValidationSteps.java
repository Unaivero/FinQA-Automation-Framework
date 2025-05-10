package com.finqa.bdd.steps;

import com.finqa.bdd.context.TestContext;
import com.finqa.reporting.visual.VisualValidator;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;

import java.io.File;

public class VisualValidationSteps {
    
    private WebDriver driver;
    private TestContext testContext;
    private String currentScreenshotName;
    
    public VisualValidationSteps(TestContext context) {
        testContext = context;
        driver = testContext.getDriver();
    }
    
    @Given("the visual validation system is initialized")
    public void theVisualValidationSystemIsInitialized() {
        VisualValidator.setupFolders();
    }
    
    @When("a screenshot is taken of the {word} page")
    public void aScreenshotIsTakenOfPage(String pageName) {
        currentScreenshotName = pageName;
        VisualValidator.takeScreenshot(driver, currentScreenshotName);
    }
    
    @When("a screenshot is taken of the {word} with name {string}")
    public void aScreenshotIsTakenOfPageWithName(String pageName, String screenshotName) {
        currentScreenshotName = screenshotName;
        VisualValidator.takeScreenshot(driver, currentScreenshotName);
    }
    
    @Then("the screenshot should match the baseline image")
    public void theScreenshotShouldMatchTheBaselineImage() {
        boolean matches = VisualValidator.compareWithBaseline(driver, currentScreenshotName);
        Assert.assertTrue(matches, "Screenshot should match baseline image");
    }
    
    @When("baseline images don't exist")
    public void baselineImagesDontExist() {
        // Simulamos esto eliminando la imagen base si existe
        String baselineFolder = System.getProperty("user.dir") + File.separator + "src" + File.separator + 
                               "test" + File.separator + "resources" + File.separator + "visual-baseline";
        File baselineFile = new File(baselineFolder + File.separator + currentScreenshotName + ".png");
        
        if (baselineFile.exists()) {
            baselineFile.delete();
        }
    }
    
    @Then("the system should create a new baseline image")
    public void theSystemShouldCreateANewBaselineImage() {
        String baselineFolder = System.getProperty("user.dir") + File.separator + "src" + File.separator + 
                               "test" + File.separator + "resources" + File.separator + "visual-baseline";
        File baselineFile = new File(baselineFolder + File.separator + currentScreenshotName + ".png");
        
        Assert.assertTrue(baselineFile.exists(), "Baseline image should be created");
    }
    
    @Then("report success for the visual comparison")
    public void reportSuccessForTheVisualComparison() {
        boolean matches = VisualValidator.compareWithBaseline(driver, currentScreenshotName);
        Assert.assertTrue(matches, "Visual comparison should be successful");
    }
    
    @Given("the browser window is resized to {string}")
    public void theBrowserWindowIsResizedTo(String resolution) {
        String[] dimensions = resolution.split("x");
        int width = Integer.parseInt(dimensions[0]);
        int height = Integer.parseInt(dimensions[1]);
        
        driver.manage().window().setSize(new Dimension(width, height));
    }
}