package com.dscommerce.controllers;

import com.dscommerce.testsupport.AbstractIntegrationTest;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AuthenticationControllerRAIT extends AbstractIntegrationTest {

    @LocalServerPort
    private int port;

    @Value("${security.client-id}")
    private String clientId;

    @Value("${security.client-secret}")
    private String clientSecret;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
    }

    @Test
    public void obtainAccessTokenShouldReturnTokenWhenCredentialsAreValid() {
        given()
                .auth().preemptive().basic(clientId, clientSecret)
                .contentType(ContentType.URLENC)
                .formParam("grant_type", "password")
                .formParam("client_id", clientId)
                .formParam("username", "matcamp1981@gmail.com")
                .formParam("password", "123456")
                .when()
                .post("/oauth2/token")
                .then()
                .statusCode(200)
                .body("access_token", notNullValue())
                .body("token_type", equalTo("Bearer"));
    }

    @Test
    public void obtainAccessTokenShouldReturnUnauthorizedWhenClientCredentialsAreInvalid() {
        given()
                .auth().preemptive().basic(clientId, clientSecret + "xyz")
                .contentType(ContentType.URLENC)
                .formParam("grant_type", "password")
                .formParam("client_id", clientId)
                .formParam("username", "matcamp1981@gmail.com")
                .formParam("password", "123456")
                .when()
                .post("/oauth2/token")
                .then()
                .statusCode(401);
    }

    @Test
    public void obtainAccessTokenShouldReturnBadRequestWhenClientCredentialsAreInvalid() {
        given()
                .auth().preemptive().basic(clientId, clientSecret)
                .contentType(ContentType.URLENC)
                .formParam("grant_type", "password")
                .formParam("client_id", clientId)
                .formParam("username", "matcamp1981@gmail.com")
                .formParam("password", "senhaErrada")
                .when()
                .post("/oauth2/token")
                .then()
                .statusCode(400);
    }
}
