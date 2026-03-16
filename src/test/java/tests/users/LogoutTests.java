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

        step("Регистрация нового пользователя", () -> {
            RegistrationBodyModel registrationData = new RegistrationBodyModel(username, password);

            SuccessfulRegistrationResponseModel registrationResponse =
                    api.users.registration(registrationData);

            assertThat(registrationResponse.id()).isGreaterThan(0);
            assertThat(registrationResponse.username()).isEqualTo(username);
        });

        LoginBodyModel loginData = new LoginBodyModel(username, password);

        String refreshToken = api.auth.loginAndGetRefreshToken(loginData);

        step("Отправка запроса logout с refresh-токеном и проверка ответа (200)", () -> {
            LogoutBodyModel logoutData = new LogoutBodyModel(refreshToken);

            Response logoutResponse = api.auth.logout(logoutData);
            assertThat(logoutResponse.body().asString()).isEqualTo("{}");
        });

        String accessToken = api.auth.loginAndGetAccessToken(loginData);
        api.users.deleteUserAuthorized(accessToken);

    }

    @Test
    @DisplayName("Тест на проверку выхода из системы не зарегистрированного пользователя")
    public void unauthorizedUserLogoutNegativeTest() {

        step("Отправка запроса logout с некорректным refresh-токеном и проверка ответа (401)", () -> {

            String refresh = "cmVmcmVzaCIsImV4cCI6MTc";
            LogoutBodyModel logoutData = new LogoutBodyModel(refresh);

            UnauthorizedUserLogoutResponseModel logoutResponse =
                    api.auth.logoutUnauthorizedUser(logoutData);

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

        step("Отправка запроса logout с пустым refresh-токеном и проверка ответа (400)", () -> {
            String refresh = "";

            LogoutBodyModel logoutData = new LogoutBodyModel(refresh);

            EmptyTokenLogoutResponseModel logoutResponse =
                    api.auth.logoutEmptyToken(logoutData);

            String expectedRefresh = "This field may not be blank.";
            String actualRefresh = logoutResponse.refresh().getFirst();

            assertThat(actualRefresh).isEqualTo(expectedRefresh);
        });
    }

    @Test
    @DisplayName("Тест на проверку выхода из системы с пустым телом запроса")
    public void emptyRequestBodyLogoutNegativeTest() {

        step("Отправка запроса logout с пустым телом  и проверка ответа (400)", () -> {

            EmptyRequestBodyLogoutResponseModel logoutResponse =
                    api.auth.logoutEmptyRequestBody();

            String expectedRefresh = "This field is required.";
            String actualRefresh = logoutResponse.refresh().getFirst();

            assertThat(actualRefresh).isEqualTo(expectedRefresh);
        });
    }

}
