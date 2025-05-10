package com.finqa.ui.tests;

import com.finqa.config.ConfigManager;
import com.finqa.config.WebDriverConfig;
import com.finqa.ui.pages.LoginPage;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;
import com.finqa.utils.TestListener;

@Listeners(TestListener.class)
public class LoginTest {
    
    private WebDriver driver;
    private LoginPage loginPage;
    
    @BeforeMethod
    public void setup() {
        driver = WebDriverConfig.initializeDriver(ConfigManager.getBrowser());
        loginPage = new LoginPage(driver);
    }
    
    @Test(description = "Verify successful login with valid credentials")
    public void testValidLogin() {
        loginPage.navigateToLoginPage(ConfigManager.getBaseUrl());
        loginPage.login(ConfigManager.getProperty("user.username"), 
                ConfigManager.getProperty("user.password"));
        
        // Add assertion for successful login (e.g., verifying redirect to dashboard)
        Assert.assertEquals(driver.getCurrentUrl(), 
                ConfigManager.getBaseUrl() + "main.jsp", 
                "User should be redirected to main page after successful login");
    }
    
    @Test(description = "Verify login fails with invalid credentials")
    public void testInvalidLogin() {
        loginPage.navigateToLoginPage(ConfigManager.getBaseUrl());
        loginPage.login("wrongUser", "wrongPass");
        
        Assert.assertTrue(loginPage.isErrorMessageDisplayed(), 
                "Error message should be displayed for invalid login");
    }
    
    @AfterMethod
    public void tearDown() {
        WebDriverConfig.quitDriver();
    }
}