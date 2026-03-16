package tests;

import io.restassured.response.Response;
import models.login.LoginBodyModel;
import models.logout.EmptyRequestBodyLogoutResponseModel;
import models.logout.EmptyTokenLogoutResponseModel;
import models.logout.LogoutBodyModel;
import models.logout.UnauthorizedUserLogoutResponseModel;
import models.registration.RegistrationBodyModel;
import models.registration.SuccessfulRegistrationResponseModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.qameta.allure.Allure.step;
import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static specs.login.LoginSpec.loginRequestSpec;
import static specs.login.LoginSpec.successfulLoginResponseSpec;
import static specs.logout.LogoutSpec.*;
import static specs.registration.RegistrationSpec.registrationRequestSpec;
import static specs.registration.RegistrationSpec.successfulRegistrationResponseSpec;

public class LogoutTests extends TestBase {
    private final TestData testData = new TestData();
    private String username;
    private String password;

    @BeforeEach
    public void prepareTestData() {
        username = testData.getUsername();
        password = testData.getPassword();
    }

    @Test
    @DisplayName("Тест на проверку выхода из системы зарегистрированного пользователя")
    public void successfulLogoutTest() {

        step("Регистрация нового пользователя", () -> {
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
        });

        LoginBodyModel loginData = new LoginBodyModel(username, password);

        String refreshToken = step("Авторизация и получение токена", () ->
                given(loginRequestSpec)
                        .body(loginData)
                        .when()
                        .post("/auth/token/")
                        .then()
                        .spec(successfulLoginResponseSpec)
                        .extract().path("refresh"));

        step("Отправка запроса logout с refresh-токеном и проверка ответа (200)", () -> {
            LogoutBodyModel logoutData = new LogoutBodyModel(refreshToken);

            Response logoutResponse = given(logoutRequestSpec)
                    .body(logoutData)
                    .when()
                    .post("/auth/logout/")
                    .then()
                    .spec(successfulLogoutResponseSpec)
                    .extract().response();
            assertThat(logoutResponse.body().asString()).isEqualTo("{}");
        });
    }

    @Test
    @DisplayName("Тест на проверку выхода из системы не зарегистрированного пользователя")
    public void unauthorizedUserLogoutNegativeTest() {

        UnauthorizedUserLogoutResponseModel logoutResponse =
                step("Отправка запроса logout с некорректным refresh-токеном и проверка ответа (401)", () -> {

                    String refresh = "cmVmcmVzaCIsImV4cCI6MTc";
                    LogoutBodyModel logoutData = new LogoutBodyModel(refresh);

                    return given(logoutRequestSpec)
                            .body(logoutData)
                            .when()
                            .post("/auth/logout/")
                            .then()
                            .spec(unauthorizedUserResponseSpec)
                            .extract().as(UnauthorizedUserLogoutResponseModel.class);
                });
        step("Проверка текста ошибки в ответе", () -> {
            String expectedDetail = "Token is invalid";
            String expectedCode = "token_not_valid";
            String actualDetail = logoutResponse.detail();
            String actualCode = logoutResponse.code();

            assertThat(actualDetail).isEqualTo(expectedDetail);
            assertThat(actualCode).isEqualTo(expectedCode);

        });
    }

    @Test
    @DisplayName("Тест на проверку выхода из системы с пустым refresh-токеном")
    public void emptyTokenLogoutNegativeTest() {

        EmptyTokenLogoutResponseModel logoutResponse = step("Отправка запроса logout с пустым refresh-токеном", () -> {
            String refresh = "";
            LogoutBodyModel logoutData = new LogoutBodyModel(refresh);

            return given(logoutRequestSpec)
                    .body(logoutData)
                    .when()
                    .post("/auth/logout/")
                    .then()
                    .spec(emptyTokenLogoutResponseSpec)
                    .extract().as(EmptyTokenLogoutResponseModel.class);
        });
        step("Проверка текста ошибки в ответе", () -> {
            String expectedRefresh = "This field may not be blank.";
            String actualRefresh = logoutResponse.refresh().getFirst();

            assertThat(actualRefresh).isEqualTo(expectedRefresh);
        });
    }

    @Test
    @DisplayName("Тест на проверку выхода из системы с пустым телом запроса")
    public void emptyRequestBodyLogoutNegativeTest() {

        EmptyRequestBodyLogoutResponseModel logoutResponse = step("Отправка запроса logout с пустым телом  и проверка ответа (400)", () -> {

            return given(logoutRequestSpec)
                    .body("{}")
                    .when()
                    .post("/auth/logout/")
                    .then()
                    .spec(emptyRequestBodyLogoutResponseSpec)
                    .extract().as(EmptyRequestBodyLogoutResponseModel.class);
        });

        step("Проверка текста ошибки в ответе", () -> {
            String expectedRefresh = "This field is required.";
            String actualRefresh = logoutResponse.refresh().getFirst();

            assertThat(actualRefresh).isEqualTo(expectedRefresh);
        });
    }

}
