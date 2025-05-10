package com.finqa.api.endpoints;

import io.restassured.response.Response;

public class AccountAPI extends BaseAPI {
    
    private static final String ACCOUNTS_ENDPOINT = "/accounts";
    
    public Response getAllAccounts(String username, String password) {
        return getAuthenticatedRequest(username, password)
                .get(ACCOUNTS_ENDPOINT);
    }
    
    public Response getAccountById(String username, String password, String accountId) {
        return getAuthenticatedRequest(username, password)
                .get(ACCOUNTS_ENDPOINT + "/" + accountId);
    }
    
    public Response getAccountTransactions(String username, String password, String accountId) {
        return getAuthenticatedRequest(username, password)
                .get(ACCOUNTS_ENDPOINT + "/" + accountId + "/transactions");
    }
}