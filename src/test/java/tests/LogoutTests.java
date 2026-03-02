package tests;

import io.restassured.response.Response;
import models.login.LoginBodyModel;
import models.login.SuccessfulLoginResponseModel;
import models.logout.EmptyRequestBodyLogoutResponseModel;
import models.logout.EmptyTokenLogoutResponseModel;
import models.logout.LogoutBodyModel;
import models.logout.UnauthorizedUserLogoutResponseModel;
import models.registration.RegistrationBodyModel;
import models.registration.SuccessfulRegistrationResponseModel;
import net.datafaker.Faker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static specs.login.LoginSpec.loginRequestSpec;
import static specs.login.LoginSpec.successfulLoginResponseSpec;
import static specs.logout.LogoutSpec.*;
import static specs.registration.RegistrationSpec.registrationRequestSpec;
import static specs.registration.RegistrationSpec.successfulRegistrationResponseSpec;

public class LogoutTests extends TestBase {

    String username;
    String password;

    @BeforeEach
    public void prepareTestData() {
        Faker faker = new Faker();
        username = faker.name().firstName();
        password = faker.name().firstName();
    }

    @Test
    public void successfulLogoutTest() {

        RegistrationBodyModel registrationData = new RegistrationBodyModel(username, password);

        SuccessfulRegistrationResponseModel registrationResponse = given(registrationRequestSpec)
                .body(registrationData)
                .when()
                .post("/users/register/")
                .then()
                .spec(successfulRegistrationResponseSpec)
                .extract()
                .as(SuccessfulRegistrationResponseModel.class);

        assertThat(registrationResponse.id()).isGreaterThan(0);
        assertThat(registrationResponse.username()).isEqualTo(username);

        LoginBodyModel loginData = new LoginBodyModel(username, password);

        SuccessfulLoginResponseModel refreshToken = given(loginRequestSpec)
                .body(loginData)
                .when()
                .post("/auth/token/")
                .then()
                .spec(successfulLoginResponseSpec)
                .extract().as(SuccessfulLoginResponseModel.class);

        String actualRefreshToken = refreshToken.refresh();

        LogoutBodyModel logoutData = new LogoutBodyModel(actualRefreshToken);

        Response logoutResponse = given(logoutRequestSpec)
                .body(logoutData)
                .when()
                .post("/auth/logout/")
                .then()
                .spec(successfulLogoutResponseSpec)
                .extract().response();

        assertThat(logoutResponse.body().asString()).isEqualTo("{}");
    }

    @Test
    public void unauthorizedUserLogoutNegativeTest() {

        String refresh = "cmVmcmVzaCIsImV4cCI6MTc";

        LogoutBodyModel logoutData = new LogoutBodyModel(refresh);

        UnauthorizedUserLogoutResponseModel logoutResponse = given(logoutRequestSpec)
                .body(logoutData)
                .when()
                .post("/auth/logout/")
                .then()
                .spec(unauthorizedUserResponseSpec)
                .extract().as(UnauthorizedUserLogoutResponseModel.class);

        String expectedDetail = "Token is invalid";
        String expectedCode = "token_not_valid";
        String actualDetail = logoutResponse.detail();
        String actualCode = logoutResponse.code();

        assertThat(actualDetail).isEqualTo(expectedDetail);
        assertThat(actualCode).isEqualTo(expectedCode);
    }

    @Test
    public void emptyTokenLogoutNegativeTest() {

        String refresh = "";

        LogoutBodyModel logoutData = new LogoutBodyModel(refresh);

        EmptyTokenLogoutResponseModel logoutResponse = given(logoutRequestSpec)
                .body(logoutData)
                .when()
                .post("/auth/logout/")
                .then()
                .spec(emptyTokenLogoutResponseSpec)
                .extract().as(EmptyTokenLogoutResponseModel.class);

        String expectedRefresh = "This field may not be blank.";
        String actualRefresh = logoutResponse.refresh().getFirst();

        assertThat(actualRefresh).isEqualTo(expectedRefresh);
    }

    @Test
    public void emptyRequestBodyLogoutNegativeTest() {

        EmptyRequestBodyLogoutResponseModel logoutResponse = given(logoutRequestSpec)
                .body("{}")
                .when()
                .post("/auth/logout/")
                .then()
                .spec(emptyRequestBodyLogoutResponseSpec)
                .extract().as(EmptyRequestBodyLogoutResponseModel.class);

        String expectedRefresh = "This field is required.";
        String actualRefresh = logoutResponse.refresh().getFirst();

        assertThat(actualRefresh).isEqualTo(expectedRefresh);
    }

}
