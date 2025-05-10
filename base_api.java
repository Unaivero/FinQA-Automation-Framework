package com.finqa.api.endpoints;

import com.finqa.config.ConfigManager;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

public class BaseAPI {
    
    protected RequestSpecification getBaseRequest() {
        return RestAssured.given()
                .baseUri(ConfigManager.getApiBaseUrl())
                .contentType(ContentType.JSON)
                .log().all();
    }
    
    protected RequestSpecification getAuthenticatedRequest(String username, String password) {
        return getBaseRequest()
                .auth()
                .preemptive()
                .basic(username, password);
    }
    
    protected Response get(String endpoint) {
        return getBaseRequest()
                .get(endpoint)
                .then()
                .log().all()
                .extract().response();
    }
    
    protected Response post(String endpoint, Object body) {
        return getBaseRequest()
                .body(body)
                .post(endpoint)
                .then()
                .log().all()
                .extract().response();
    }
    
    protected Response put(String endpoint, Object body) {
        return getBaseRequest()
                .body(body)
                .put(endpoint)
                .then()
                .log().all()
                .extract().response();
    }
    
    protected Response delete(String endpoint) {
        return getBaseRequest()
                .delete(endpoint)
                .then()
                .log().all()
                .extract().response();
    }
}