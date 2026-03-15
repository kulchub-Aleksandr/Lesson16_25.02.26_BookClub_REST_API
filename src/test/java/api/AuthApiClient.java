package api;

import io.qameta.allure.Step;
import io.restassured.response.Response;
import models.login.*;
import models.logout.EmptyRequestBodyLogoutResponseModel;
import models.logout.EmptyTokenLogoutResponseModel;
import models.logout.LogoutBodyModel;
import models.logout.UnauthorizedUserLogoutResponseModel;

import static io.restassured.RestAssured.given;
import static specs.login.LoginSpec.*;
import static specs.logout.LogoutSpec.*;
import static specs.registration.RegistrationSpec.registrationRequestSpec;

public class AuthApiClient {
    @Step("Авторизация существующего пользователя и проверка ответа (200)")
    public SuccessfulLoginResponseModel login(LoginBodyModel loginBody) {
        return given(registrationRequestSpec)
                .body(loginBody)
                .when()
                .post("/auth/token/")
                .then()
                .spec(successfulLoginResponseSpec)
                .extract()
                .as(SuccessfulLoginResponseModel.class);
    }

    @Step("Авторизация и получение access-токена пользователя")
    public String loginAndGetAccessToken(LoginBodyModel loginBody) {
        return given(loginRequestSpec)
                .body(loginBody)
                .when()
                .post("/auth/token/")
                .then()
                .spec(successfulLoginResponseSpec)
                .extract()
                .path("access");
    }

    @Step("Авторизация и получение refresh-токена")
    public String loginAndGetRefreshToken(LoginBodyModel loginBody) {
        return given(loginRequestSpec)
                .body(loginBody)
                .when()
                .post("/auth/token/")
                .then()
                .spec(successfulLoginResponseSpec)
                .extract()
                .path("refresh");
    }

    public WrongCredentialsLoginResponseModel
    loginWrongCredentials(LoginBodyModel loginBody) {
        return given(loginRequestSpec)
                .body(loginBody)
                .when()
                .post("/auth/token/")
                .then()
                .spec(wrongCredentialsLoginResponseSpec)
                .extract()
                .as(WrongCredentialsLoginResponseModel.class);
    }

    public EmptyPasswordResponseModel
    loginEmptyPassword(LoginBodyModel loginBody) {
        return given(loginRequestSpec)
                .body(loginBody)
                .when()
                .post("/auth/token/")
                .then()
                .spec(emptyPasswordLoginResponseSpec)
                .extract()
                .as(EmptyPasswordResponseModel.class);
    }

    public EmptyUserResponseModel
    loginEmptyUser(LoginBodyModel loginBody) {
        return given(loginRequestSpec)
                .body(loginBody)
                .when()
                .post("/auth/token/")
                .then()
                .spec(emptyUserLoginResponseSpec)
                .extract()
                .as(EmptyUserResponseModel.class);
    }

    public EmptyUserEmptyPasswordResponseModel
    loginEmptyUserEmptyPassword(LoginBodyModel loginBody) {
        return given(loginRequestSpec)
                .body(loginBody)
                .when()
                .post("/auth/token/")
                .then()
                .spec(emptyUserEmptyPasswordLoginResponseSpec)
                .extract()
                .as(EmptyUserEmptyPasswordResponseModel.class);
    }

    @Step("Отправка запроса logout")
    public Response logout(LogoutBodyModel logoutBody) {
        return given(logoutRequestSpec)
                .body(logoutBody)
                .when()
                .post("/auth/logout/")
                .then()
                .spec(successfulLogoutResponseSpec)
                .extract().response();
    }

    public UnauthorizedUserLogoutResponseModel
    logoutUnauthorizedUser(LogoutBodyModel logoutBody) {
        return given(logoutRequestSpec)
                .body(logoutBody)
                .when()
                .post("/auth/logout/")
                .then()
                .spec(unauthorizedUserResponseSpec)
                .extract().as(UnauthorizedUserLogoutResponseModel.class);
    }

    public EmptyTokenLogoutResponseModel
    logoutEmptyToken(LogoutBodyModel logoutBody) {
        return given(logoutRequestSpec)
                .body(logoutBody)
                .when()
                .post("/auth/logout/")
                .then()
                .spec(emptyTokenLogoutResponseSpec)
                .extract().as(EmptyTokenLogoutResponseModel.class);
    }

    public EmptyRequestBodyLogoutResponseModel
    logoutEmptyRequestBody() {
        return given(logoutRequestSpec)
                .body("{}")
                .when()
                .post("/auth/logout/")
                .then()
                .spec(emptyRequestBodyLogoutResponseSpec)
                .extract().as(EmptyRequestBodyLogoutResponseModel.class);
    }


}
