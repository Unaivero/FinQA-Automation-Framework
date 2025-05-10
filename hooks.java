package com.finqa.bdd.steps;

import com.finqa.bdd.context.TestContext;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

public class Hooks {
    
    private TestContext testContext;
    
    public Hooks(TestContext context) {
        testContext = context;
    }
    
    @Before
    public void setUp(Scenario scenario) {
        System.out.println("Starting scenario: " + scenario.getName());
    }
    
    @After
    public void tearDown(Scenario scenario) {
        WebDriver driver = testContext.getDriver();
        
        if (driver != null) {
            if (scenario.isFailed()) {
                // Tomar screenshot en caso de fallo
                byte[] screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
                scenario.attach(screenshot, "image/png", "Screenshot of failure");
            }
            
            // Cerrar navegador
            testContext.quitDriver();
        }
        
        System.out.println("Completed scenario: " + scenario.getName());
    }
}