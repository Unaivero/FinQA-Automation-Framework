package com.finqa.bdd.steps;

import com.finqa.bdd.context.TestContext;
import com.finqa.config.ConfigManager;
import com.finqa.ui.pages.LoginPage;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;

public class LoginSteps {
    
    private WebDriver driver;
    private LoginPage loginPage;
    private TestContext testContext;
    
    public LoginSteps(TestContext context) {
        testContext = context;
        driver = testContext.getDriver();
        loginPage = new LoginPage(driver);
    }
    
    @Given("the user is on the login page")
    public void theUserIsOnTheLoginPage() {
        loginPage.navigateToLoginPage(ConfigManager.getBaseUrl());
    }
    
    @When("the user enters valid username {string}")
    public void theUserEntersValidUsername(String username) {
        loginPage.enterUsername(username);
    }
    
    @When("the user enters valid password {string}")
    public void theUserEntersValidPassword(String password) {
        loginPage.enterPassword(password);
    }
    
    @When("the user enters invalid username {string}")
    public void theUserEntersInvalidUsername(String username) {
        loginPage.enterUsername(username);
    }
    
    @When("the user enters invalid password {string}")
    public void theUserEntersInvalidPassword(String password) {
        loginPage.enterPassword(password);
    }
    
    @When("the user enters username {string}")
    public void theUserEntersUsername(String username) {
        loginPage.enterUsername(username);
    }
    
    @When("the user enters password {string}")
    public void theUserEntersPassword(String password) {
        loginPage.enterPassword(password);
    }
    
    @When("the user clicks the login button")
    public void theUserClicksTheLoginButton() {
        loginPage.clickLoginButton();
    }
    
    @Then("the user should be redirected to the dashboard")
    public void theUserShouldBeRedirectedToTheDashboard() {
        Assert.assertEquals(driver.getCurrentUrl(), 
                ConfigManager.getBaseUrl() + "main.jsp", 
                "User should be redirected to main page after successful login");
    }
    
    @Then("the user should see their account summary")
    public void theUserShouldSeeTheirAccountSummary() {
        // Aquí podríamos verificar elementos específicos del dashboard
        // Por ejemplo: Assert.assertTrue(dashboardPage.isAccountSummaryDisplayed());
        
        // Para este ejemplo, simplemente verificamos que estamos en la página correcta
        Assert.assertTrue(driver.getTitle().contains("Altoro Mutual"), 
                "Dashboard should display Altoro Mutual title");
    }
    
    @Then("the user should see an error message")
    public void theUserShouldSeeAnErrorMessage() {
        Assert.assertTrue(loginPage.isErrorMessageDisplayed(), 
                "Error message should be displayed for invalid login");
    }
    
    @Then("the user should remain on the login page")
    public void theUserShouldRemainOnTheLoginPage() {
        Assert.assertTrue(driver.getCurrentUrl().contains("login"), 
                "User should remain on login page after failed login");
    }
    
    @Then("the system should respond with {string}")
    public void theSystemShouldRespondWith(String expectedResult) {
        switch (expectedResult) {
            case "successful login":
            case "successful admin login":
                Assert.assertEquals(driver.getCurrentUrl(), 
                        ConfigManager.getBaseUrl() + "main.jsp", 
                        "User should be redirected to main page after successful login");
                break;
            case "authentication failed":
                Assert.assertTrue(loginPage.isErrorMessageDisplayed(), 
                        "Error message should be displayed for invalid login");
                break;
            case "username cannot be empty":
            case "password cannot be empty":
                Assert.assertTrue(loginPage.isErrorMessageDisplayed(), 
                        "Error message should be displayed for empty fields");
                break;
            default:
                throw new IllegalArgumentException("Unsupported response type: " + expectedResult);
        }
    }
}