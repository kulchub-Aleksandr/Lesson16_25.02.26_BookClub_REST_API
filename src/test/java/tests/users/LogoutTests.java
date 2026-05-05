package tests.users;

import allure.Layer;
import io.qameta.allure.Story;
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
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import tests.TestBase;
import tests.TestData;

import static io.qameta.allure.Allure.step;
import static org.assertj.core.api.Assertions.assertThat;


public class LogoutTests extends TestBase {
    private final TestData testData = new TestData();
    private String username;
    private String password;
    String wrongRefreshToken = "cmVmcmVzaCIsImV4cCI6MTc";
    String emptyRefreshToken = "";

    @BeforeEach
    public void prepareTestData() {
        username = testData.getUsername();
        password = testData.getPassword();
    }

    @Test
    @Story("Выход пользователя из системы")
    @Tag("API")
    @DisplayName("Тест на проверку выхода из системы зарегистрированного пользователя")
    public void successfulLogoutTest() {

        SuccessfulRegistrationResponseModel registrationResponse
                = api.users.registration(new RegistrationBodyModel(username, password));
        step("Проверка корректности зарегистрированных данных", () -> {
            assertThat(registrationResponse.id()).isGreaterThan(0);
            assertThat(registrationResponse.username()).isEqualTo(username);
        });

        String refreshToken
                = api.auth.loginAndGetRefreshToken(new LoginBodyModel(username, password));

        step("Проверка, что ответ logout — пустой объект {}", () -> {
            Response logoutResponse = api.auth.logout(new LogoutBodyModel(refreshToken));
            assertThat(logoutResponse.body().asString()).isEqualTo("{}");
        });
        String accessToken = api.auth.loginAndGetAccessToken(new LoginBodyModel(username, password));
        api.users.deleteUserAuthorized(accessToken);
    }

    @Test
    @Story("Выход пользователя из системы")
    @Tag("API")
    @DisplayName("Тест на проверку выхода из системы не зарегистрированного пользователя")
    public void unauthorizedUserLogoutNegativeTest() {

        UnauthorizedUserLogoutResponseModel logoutResponse
                = api.auth.logoutUnauthorizedUser(new LogoutBodyModel(wrongRefreshToken));

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
    @Story("Выход пользователя из системы")
    @Tag("API")
    @DisplayName("Тест на проверку выхода из системы с пустым refresh-токеном")
    public void emptyTokenLogoutNegativeTest() {

        EmptyTokenLogoutResponseModel logoutResponse
                = api.auth.logoutEmptyToken(new LogoutBodyModel(emptyRefreshToken));

        step("Проверка текста ошибки в ответе", () -> {
            String expectedRefresh = "This field may not be blank.";
            String actualRefresh = logoutResponse.refresh().getFirst();
            assertThat(actualRefresh).isEqualTo(expectedRefresh);
        });
    }

    @Test
    @Story("Выход пользователя из системы")
    @Tag("API")
    @DisplayName("Тест на проверку выхода из системы с пустым телом запроса")
    public void emptyRequestBodyLogoutNegativeTest() {

        EmptyRequestBodyLogoutResponseModel logoutResponse
                = api.auth.logoutEmptyRequestBody();
        step("Проверка текста ошибки в ответе", () -> {
            String expectedRefresh = "This field is required.";
            String actualRefresh = logoutResponse.refresh().getFirst();

            assertThat(actualRefresh).isEqualTo(expectedRefresh);
        });
    }
}
