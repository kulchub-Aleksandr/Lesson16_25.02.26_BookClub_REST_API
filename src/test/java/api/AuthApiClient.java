package api;

import io.qameta.allure.Step;
import io.restassured.response.Response;
import models.users.login.*;
import models.users.logout.EmptyRequestBodyLogoutResponseModel;
import models.users.logout.EmptyTokenLogoutResponseModel;
import models.users.logout.LogoutBodyModel;
import models.users.logout.UnauthorizedUserLogoutResponseModel;

import static io.restassured.RestAssured.given;
import static specs.users.login.LoginSpec.*;
import static specs.users.logout.LogoutSpec.*;
import static specs.users.registration.RegistrationSpec.registrationRequestSpec;

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

    @Step("Авторизация с применением не правильного пароля и проверка ответа (401)")
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

    @Step("Авторизация с применением пустого поля для пароля и проверка ответа (400)")
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

    @Step("Авторизация с применением пустого поля для логина и проверка ответа (400)")
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

    @Step("Авторизация с применением пустого поля для логина и пароля с проверкой ответа (400)")
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

    @Step("Отправка запроса logout с некорректным refresh-токеном и проверка ответа (401)")
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

    @Step("Отправка запроса logout с пустымм refresh-токеном и проверка ответа (400)")
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

    @Step("Отправка запроса logout с пустым телом  и проверка ответа (400)")
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
