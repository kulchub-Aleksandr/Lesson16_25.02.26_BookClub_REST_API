package tests.users;

import models.users.login.*;
import models.users.registration.RegistrationBodyModel;
import models.users.registration.SuccessfulRegistrationResponseModel;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import tests.TestBase;
import tests.TestData;

import static io.qameta.allure.Allure.step;
import static org.assertj.core.api.Assertions.assertThat;

public class LoginTests extends TestBase {
    private final TestData testData = new TestData();
    private String username;
    private String password;
    private String wrongPassword;
    private String wrongUsername;
    private String emptyPassword;
    private String emptyUsername;

    @BeforeEach
    public void prepareTestData() {
        username = testData.getUsername();
        password = testData.getPassword();
        wrongPassword = password + "1234";
        wrongUsername = username + "qa";
        emptyPassword = testData.getEmptyPassword();
        emptyUsername = testData.getEmptyUsername();

    }

    @AfterEach
    void cleanUpTestUsers() {
        if (username != null && password != null) {
            LoginBodyModel loginData = new LoginBodyModel(username, password);
            String accessToken = api.auth.loginAndGetAccessToken(loginData);
            api.users.deleteUserAuthorized(accessToken);

        }
    }

    @Test
    @DisplayName("Тест на проверку авторизации существующего пользователя")
    public void successfulLoginTest() {

        step("Регистрация нового пользователя", () -> {
            RegistrationBodyModel registrationData = new RegistrationBodyModel(username, password);

            SuccessfulRegistrationResponseModel registrationResponse =
                    api.users.registration(registrationData);

            assertThat(registrationResponse.id()).isGreaterThan(0);
            assertThat(registrationResponse.username()).isEqualTo(username);
        });

        LoginBodyModel loginData = new LoginBodyModel(username, password);
        step("Авторизация", () -> {
            SuccessfulLoginResponseModel loginResponse =
                    api.auth.login(loginData);

            String expectedTokenPath = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9";

            String actualAccess = loginResponse.access();
            String actualRefresh = loginResponse.refresh();

            assertThat(actualAccess).startsWith(expectedTokenPath);
            assertThat(actualRefresh).startsWith(expectedTokenPath);
            assertThat(actualAccess).isNotEqualTo(actualRefresh);
        });

    }

    @Test
    @DisplayName("Тест на проверку авторизации пользователя с применением не правильного пароля")
    public void wrongPasswordLoginNegativeTest() {

        step("Регистрация нового пользователя", () -> {
            RegistrationBodyModel registrationData = new RegistrationBodyModel(username, password);

            SuccessfulRegistrationResponseModel registrationResponse =
                    api.users.registration(registrationData);

            assertThat(registrationResponse.id()).isGreaterThan(0);
            assertThat(registrationResponse.username()).isEqualTo(username);
        });

        LoginBodyModel wrongLoginData = new LoginBodyModel(username, wrongPassword);

        step("Авторизация с применением не правильного пароля и проверка ответа (401)", () -> {
            WrongCredentialsLoginResponseModel loginResponse =
                    api.auth.loginWrongCredentials(wrongLoginData);

            String expectedDetailError = "Invalid username or password.";
            String actualDetailError = loginResponse.detail();

            assertThat(actualDetailError).isEqualTo(expectedDetailError);
        });
    }

    @Test
    @DisplayName("Тест на проверку авторизации пользователя с применением пустого поля для пароля")
    public void emptyPasswordLoginNegativeTest() {
        step("Регистрация нового пользователя", () -> {
            RegistrationBodyModel registrationData = new RegistrationBodyModel(username, password);

            SuccessfulRegistrationResponseModel registrationResponse =
                    api.users.registration(registrationData);

            assertThat(registrationResponse.id()).isGreaterThan(0);
            assertThat(registrationResponse.username()).isEqualTo(username);
        });

        LoginBodyModel wrongLoginData = new LoginBodyModel(username, emptyPassword);
        step("Авторизация с применением пустого поля для пароля и проверка ответа (400)", () -> {
            EmptyPasswordResponseModel loginResponse =
                    api.auth.loginEmptyPassword(wrongLoginData);

            String expectedDetailError = "This field may not be blank.";
            String actualDetailError = loginResponse.password().getFirst();

            assertThat(actualDetailError).isEqualTo(expectedDetailError);
        });
    }

    @Test
    @DisplayName("Тест на проверку авторизации пользователя с применением не правильного логина")
    public void wrongUserNameLoginNegativeTest() {

        step("Регистрация нового пользователя", () -> {
            RegistrationBodyModel registrationData = new RegistrationBodyModel(username, password);

            SuccessfulRegistrationResponseModel registrationResponse =
                    api.users.registration(registrationData);

            assertThat(registrationResponse.id()).isGreaterThan(0);
            assertThat(registrationResponse.username()).isEqualTo(username);
        });

        LoginBodyModel wrongLoginData = new LoginBodyModel(wrongUsername, password);

        step("Авторизация с применением не правильного логина и проверка ответа (401)", () -> {
            WrongCredentialsLoginResponseModel loginResponse =
                    api.auth.loginWrongCredentials(wrongLoginData);

            String expectedDetailError = "Invalid username or password.";
            String actualDetailError = loginResponse.detail();

            assertThat(actualDetailError).isEqualTo(expectedDetailError);
        });

    }

    @Test
    @DisplayName("Тест на проверку авторизации пользователя с применением пустого поля для логина")
    public void emptyUserNameLoginNegativeTest() {
        step("Регистрация нового пользователя", () -> {
            RegistrationBodyModel registrationData = new RegistrationBodyModel(username, password);

            SuccessfulRegistrationResponseModel registrationResponse =
                    api.users.registration(registrationData);

            assertThat(registrationResponse.id()).isGreaterThan(0);
            assertThat(registrationResponse.username()).isEqualTo(username);
        });

        LoginBodyModel wrongLoginData = new LoginBodyModel(emptyUsername, password);

        step("Авторизация с применением пустого поля для логина и проверка ответа (400)", () -> {
            EmptyUserResponseModel loginResponse =
                    api.auth.loginEmptyUser(wrongLoginData);

            String expectedDetailError = "This field may not be blank.";
            String actualDetailError = loginResponse.username().getFirst();

            assertThat(actualDetailError).isEqualTo(expectedDetailError);
        });

    }

    @Test
    @DisplayName("Тест на проверку авторизации пользователя с применением пустого поля для логина и пустого поля для пароля")
    public void emptyUserNameEmptyPasswordLoginNegativeTest() {
        step("Регистрация нового пользователя", () -> {
            RegistrationBodyModel registrationData = new RegistrationBodyModel(username, password);

            SuccessfulRegistrationResponseModel registrationResponse =
                    api.users.registration(registrationData);

            assertThat(registrationResponse.id()).isGreaterThan(0);
            assertThat(registrationResponse.username()).isEqualTo(username);
        });

        LoginBodyModel wrongLoginData = new LoginBodyModel(emptyUsername, emptyPassword);
        step("Авторизация с применением пустого поля для логина и пароля с проверкой ответа (400)", () -> {
            EmptyUserEmptyPasswordResponseModel loginResponse =
                    api.auth.loginEmptyUserEmptyPassword(wrongLoginData);

            String expectedDetailError = "This field may not be blank.";
            String actualDetailError_1 = loginResponse.username().getFirst();
            String actualDetailError_2 = loginResponse.password().getFirst();

            assertThat(actualDetailError_1).isEqualTo(expectedDetailError);
            assertThat(actualDetailError_2).isEqualTo(expectedDetailError);
        });
    }
}
