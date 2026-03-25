package tests.users;

import io.restassured.response.Response;
import models.users.login.LoginBodyModel;
import models.users.logout.EmptyRequestBodyLogoutResponseModel;
import models.users.logout.EmptyTokenLogoutResponseModel;
import models.users.logout.LogoutBodyModel;
import models.users.logout.UnauthorizedUserLogoutResponseModel;
import models.users.registration.RegistrationBodyModel;
import models.users.registration.SuccessfulRegistrationResponseModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import tests.TestBase;
import tests.TestData;

import static io.qameta.allure.Allure.step;
import static org.assertj.core.api.Assertions.assertThat;

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

        SuccessfulRegistrationResponseModel registrationResponse
                = step("Регистрация нового пользователя и проверка ответа (201)", () -> {
            RegistrationBodyModel registrationData = new RegistrationBodyModel(username, password);
            return api.users.registration(registrationData);
        });
        step("Проверка корректности зарегистрированных данных", () -> {
            assertThat(registrationResponse.id()).isGreaterThan(0);
            assertThat(registrationResponse.username()).isEqualTo(username);
        });

        String refreshToken =
                step("Отправка запроса logout с refresh-токеном и проверка ответа (200)", () -> {
                    LoginBodyModel loginData = new LoginBodyModel(username, password);

                    return api.auth.loginAndGetRefreshToken(loginData);
                });
        step("Проверка, что ответ logout — пустой объект {}", () -> {
            LogoutBodyModel logoutData = new LogoutBodyModel(refreshToken);

            Response logoutResponse = api.auth.logout(logoutData);
            assertThat(logoutResponse.body().asString()).isEqualTo("{}");
        });
        String accessToken = step("Авторизация и получение access-токена для удаления пользователя", () -> {
            LoginBodyModel loginData = new LoginBodyModel(username, password);
            return api.auth.loginAndGetAccessToken(loginData);
        });
        api.users.deleteUserAuthorized(accessToken);
    }

    @Test
    @DisplayName("Тест на проверку выхода из системы не зарегистрированного пользователя")
    public void unauthorizedUserLogoutNegativeTest() {

        UnauthorizedUserLogoutResponseModel logoutResponse =
                step("Отправка запроса logout с некорректным refresh-токеном и проверка ответа (401)", () -> {
                    String refresh = "cmVmcmVzaCIsImV4cCI6MTc";
                    LogoutBodyModel logoutData = new LogoutBodyModel(refresh);

                    return api.auth.logoutUnauthorizedUser(logoutData);
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

        EmptyTokenLogoutResponseModel logoutResponse =
                step("Отправка запроса logout с пустым refresh-токеном и проверка ответа (400)", () -> {
                    String refresh = "";
                    LogoutBodyModel logoutData = new LogoutBodyModel(refresh);
                    return api.auth.logoutEmptyToken(logoutData);
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

        EmptyRequestBodyLogoutResponseModel logoutResponse =
                step("Отправка запроса logout с пустым телом  и проверка ответа (400)",
                        api.auth::logoutEmptyRequestBody);
        step("Проверка текста ошибки в ответе", () -> {
            String expectedRefresh = "This field is required.";
            String actualRefresh = logoutResponse.refresh().getFirst();

            assertThat(actualRefresh).isEqualTo(expectedRefresh);
        });
    }
}
