package tests;

import models.login.*;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static specs.login.LoginSpec.*;

public class LoginTests extends TestBase {

    String username = "qaguru";
    String password = "qaguru123";
    String wrongPassword = "qaguru1234";
    String wrongUsername = "qaguruqa";
    String emptyPassword = "";
    String emptyUsername = "";

    @Test
    public void successfulLoginTest(){
        LoginBodyModel loginData = new LoginBodyModel(username, password);

        SuccessfulLoginResponseModel loginResponse = given(loginRequestSpec)
                .body(loginData)
                .when()
                .post("/auth/token/")
                .then()
                .spec(successfulLoginResponseSpec)
                .extract()
                .as(SuccessfulLoginResponseModel.class);

        String expectedTokenPath = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9";
        String actualAccess = loginResponse.access();
        String actualRefresh = loginResponse.refresh();

        assertThat(actualAccess).startsWith(expectedTokenPath);
        assertThat(actualRefresh).startsWith(expectedTokenPath);
        assertThat(actualAccess).isNotEqualTo(actualRefresh);
    }

    @Test
    public void wrongPasswordLoginTest(){
        LoginBodyModel loginData = new LoginBodyModel(username, wrongPassword);

        WrongCredentialsLoginResponseModel loginResponse = given(loginRequestSpec)
                .body(loginData)
                .when()
                .post("/auth/token/")
                .then()
                .spec(wrongCredentialsLoginResponseSpec)
                .extract()
                .as(WrongCredentialsLoginResponseModel.class);

        String expectedDetailError = "Invalid username or password.";
        String actualDetailError = loginResponse.detail();

        assertThat(actualDetailError).isEqualTo(expectedDetailError);
    }

    @Test
    public void emptyPasswordLoginTest(){
        LoginBodyModel loginData = new LoginBodyModel(username, emptyPassword);

        EmptyPasswordResponseModel loginResponse = given(loginRequestSpec)
                .body(loginData)
                .when()
                .post("/auth/token/")
                .then()
                .spec(emptyPasswordLoginResponseSpec)
                .extract()
                .as(EmptyPasswordResponseModel.class);

        String expectedDetailError = "This field may not be blank.";
        String actualDetailError = loginResponse.password().getFirst();

        assertThat(actualDetailError).isEqualTo(expectedDetailError);
    }

    @Test
    public void wrongUserNameLoginTest(){
        LoginBodyModel loginData = new LoginBodyModel(wrongUsername, password);

        WrongCredentialsLoginResponseModel loginResponse = given(loginRequestSpec)
                .body(loginData)
                .when()
                .post("/auth/token/")
                .then()
                .spec(wrongCredentialsLoginResponseSpec)
                .extract()
                .as(WrongCredentialsLoginResponseModel.class);

        String expectedDetailError = "Invalid username or password.";
        String actualDetailError = loginResponse.detail();

        assertThat(actualDetailError).isEqualTo(expectedDetailError);
    }

    @Test
    public void emptyUserNameLoginTest(){
        LoginBodyModel loginData = new LoginBodyModel(emptyUsername, password);

        EmptyUserResponseModel loginResponse = given(loginRequestSpec)
                .body(loginData)
                .when()
                .post("/auth/token/")
                .then()
                .spec(emptyUserLoginResponseSpec)
                .extract()
                .as(EmptyUserResponseModel.class);

        String expectedDetailError = "This field may not be blank.";
        String actualDetailError = loginResponse.username().getFirst();

        assertThat(actualDetailError).isEqualTo(expectedDetailError);
    }

    @Test
    public void emptyUserNameEmptyPasswordLoginTest(){
        LoginBodyModel loginData = new LoginBodyModel(emptyUsername, emptyPassword);

        EmptyUserEmptyPasswordResponseModel loginResponse = given(loginRequestSpec)
                .body(loginData)
                .when()
                .post("/auth/token/")
                .then()
                .spec(emptyUserEmptyPasswordLoginResponseSpec)
                .extract()
                .as(EmptyUserEmptyPasswordResponseModel.class);

        String expectedDetailError = "This field may not be blank.";
        String actualDetailError_1 = loginResponse.username().getFirst();
        String actualDetailError_2 = loginResponse.password().getFirst();

        assertThat(actualDetailError_1).isEqualTo(expectedDetailError);
        assertThat(actualDetailError_2).isEqualTo(expectedDetailError);
    }

}
