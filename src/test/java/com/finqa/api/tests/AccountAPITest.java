package com.finqa.api.tests;

import com.finqa.api.endpoints.AccountAPI;
import com.finqa.config.ConfigManager;
import io.qameta.allure.Description;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Story;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class AccountAPITest {
    
    private AccountAPI accountAPI;
    private String username;
    private String password;
    
    @BeforeClass
    public void setup() {
        accountAPI = new AccountAPI();
        username = ConfigManager.getProperty("user.username");
        password = ConfigManager.getProperty("user.password");
    }
    
    @Test
    @Severity(SeverityLevel.NORMAL)
    @Description("Verify that transactions for a specific account can be retrieved")
    public void testGetAccountTransactions() {
        // Assuming account ID 800000 exists
        String accountId = "800000";
        Response response = accountAPI.getAccountTransactions(username, password, accountId);
        
        Assert.assertEquals(response.getStatusCode(), 200, "Status code should be 200");
    }
    
    @Test
    @Severity(SeverityLevel.NORMAL)
    @Description("Verify that a specific account can be retrieved by ID")
    public void testGetAccountById() {
        // Assuming account ID 800000 exists
        String accountId = "800000";
        Response response = accountAPI.getAccountById(username, password, accountId);
        
        Assert.assertEquals(response.getStatusCode(), 200, "Status code should be 200");
        Assert.assertEquals(response.jsonPath().getString("id"), accountId, 
                "Returned account ID should match the requested ID");
    }
    
    @Test
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verify that all accounts can be retrieved successfully")
    public void testGetAllAccounts() {
        Response response = accountAPI.getAllAccounts(username, password);
        
        Assert.assertEquals(response.getStatusCode(), 200, "Status code should be 200");
        Assert.assertFalse(response.jsonPath().getList("$").isEmpty(), "Accounts list should not be empty");
    }
}