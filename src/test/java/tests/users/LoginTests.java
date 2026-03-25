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

        SuccessfulRegistrationResponseModel registrationResponse
                = step("Регистрация нового пользователя и проверка ответа (201)", () -> {
            RegistrationBodyModel registrationData = new RegistrationBodyModel(username, password);
            return api.users.registration(registrationData);
        });
        step("Проверка корректности зарегистрированных данных", () -> {
            assertThat(registrationResponse.id()).isGreaterThan(0);
            assertThat(registrationResponse.username()).isEqualTo(username);
        });


        SuccessfulLoginResponseModel loginResponse = step("Авторизация", () -> {
            LoginBodyModel loginData = new LoginBodyModel(username, password);
            return api.auth.login(loginData);
        });
        step("Проверка корректности полученных токенов", () -> {
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

        SuccessfulRegistrationResponseModel registrationResponse =
                step("Регистрация нового пользователя и проверка ответа (201)", () -> {
                    RegistrationBodyModel registrationData = new RegistrationBodyModel(username, password);
                    return api.users.registration(registrationData);
                });
        step("Проверка корректности зарегистрированных данных", () -> {
            assertThat(registrationResponse.id()).isGreaterThan(0);
            assertThat(registrationResponse.username()).isEqualTo(username);
        });

        WrongCredentialsLoginResponseModel loginResponse =
                step("Авторизация с применением не правильного пароля и проверка ответа (401)", () -> {
                    LoginBodyModel wrongLoginData = new LoginBodyModel(username, wrongPassword);
                    return api.auth.loginWrongCredentials(wrongLoginData);
                });
        step("Проверка текста ошибки в ответе", () -> {
            String expectedDetailError = "Invalid username or password.";
            String actualDetailError = loginResponse.detail();

            assertThat(actualDetailError).isEqualTo(expectedDetailError);
        });
    }

    @Test
    @DisplayName("Тест на проверку авторизации пользователя с применением пустого поля для пароля")
    public void emptyPasswordLoginNegativeTest() {
        SuccessfulRegistrationResponseModel registrationResponse =
                step("Регистрация нового пользователя и проверка ответа (201)", () -> {
                    RegistrationBodyModel registrationData = new RegistrationBodyModel(username, password);
                    return api.users.registration(registrationData);
                });
        step("Проверка корректности зарегистрированных данных", () -> {
            assertThat(registrationResponse.id()).isGreaterThan(0);
            assertThat(registrationResponse.username()).isEqualTo(username);
        });

        EmptyPasswordResponseModel loginResponse =
                step("Авторизация с применением пустого поля для пароля и проверка ответа (400)", () -> {
                    LoginBodyModel wrongLoginData = new LoginBodyModel(username, emptyPassword);
                    return api.auth.loginEmptyPassword(wrongLoginData);
                });
        step("Проверка текста ошибки в ответе", () -> {
            String expectedDetailError = "This field may not be blank.";
            String actualDetailError = loginResponse.password().getFirst();

            assertThat(actualDetailError).isEqualTo(expectedDetailError);
        });
    }

    @Test
    @DisplayName("Тест на проверку авторизации пользователя с применением не правильного логина")
    public void wrongUserNameLoginNegativeTest() {
        SuccessfulRegistrationResponseModel registrationResponse =
                step("Регистрация нового пользователя и проверка ответа (201)", () -> {
                    RegistrationBodyModel registrationData = new RegistrationBodyModel(username, password);
                    return api.users.registration(registrationData);
                });
        step("Проверка корректности зарегистрированных данных", () -> {
            assertThat(registrationResponse.id()).isGreaterThan(0);
            assertThat(registrationResponse.username()).isEqualTo(username);
        });

        WrongCredentialsLoginResponseModel loginResponse =
                step("Авторизация с применением не правильного логина и проверка ответа (401)", () -> {
                    LoginBodyModel wrongLoginData = new LoginBodyModel(wrongUsername, password);
                    return api.auth.loginWrongCredentials(wrongLoginData);
                });
        step("Проверка текста ошибки в ответе", () -> {
            String expectedDetailError = "Invalid username or password.";
            String actualDetailError = loginResponse.detail();
            assertThat(actualDetailError).isEqualTo(expectedDetailError);
        });

    }

    @Test
    @DisplayName("Тест на проверку авторизации пользователя с применением пустого поля для логина")
    public void emptyUserNameLoginNegativeTest() {
        SuccessfulRegistrationResponseModel registrationResponse =
                step("Регистрация нового пользователя и проверка ответа (201)", () -> {
                    RegistrationBodyModel registrationData = new RegistrationBodyModel(username, password);
                    return api.users.registration(registrationData);
                });
        step("Проверка корректности зарегистрированных данных", () -> {
            assertThat(registrationResponse.id()).isGreaterThan(0);
            assertThat(registrationResponse.username()).isEqualTo(username);
        });

        EmptyUserResponseModel loginResponse =
                step("Авторизация с применением пустого поля для логина и проверка ответа (400)", () -> {
                    LoginBodyModel wrongLoginData = new LoginBodyModel(emptyUsername, password);
                    return api.auth.loginEmptyUser(wrongLoginData);
                });
        step("Проверка текста ошибки в ответе", () -> {
            String expectedDetailError = "This field may not be blank.";
            String actualDetailError = loginResponse.username().getFirst();

            assertThat(actualDetailError).isEqualTo(expectedDetailError);
        });
    }

    @Test
    @DisplayName("Тест на проверку авторизации пользователя с применением пустого поля для логина и пустого поля для пароля")
    public void emptyUserNameEmptyPasswordLoginNegativeTest() {
        SuccessfulRegistrationResponseModel registrationResponse =
                step("Регистрация нового пользователя и проверка ответа (201)", () -> {
                    RegistrationBodyModel registrationData = new RegistrationBodyModel(username, password);
                    return api.users.registration(registrationData);
                });
        step("Проверка корректности зарегистрированных данных", () -> {
            assertThat(registrationResponse.id()).isGreaterThan(0);
            assertThat(registrationResponse.username()).isEqualTo(username);
        });

        EmptyUserEmptyPasswordResponseModel loginResponse =
                step("Авторизация с применением пустого поля для логина и пароля с проверкой ответа (400)", () -> {
                    LoginBodyModel wrongLoginData = new LoginBodyModel(emptyUsername, emptyPassword);
                    return api.auth.loginEmptyUserEmptyPassword(wrongLoginData);
                });
        step("Проверка текста ошибки в ответе", () -> {
            String expectedDetailError = "This field may not be blank.";
            String actualDetailError_1 = loginResponse.username().getFirst();
            String actualDetailError_2 = loginResponse.password().getFirst();

            assertThat(actualDetailError_1).isEqualTo(expectedDetailError);
            assertThat(actualDetailError_2).isEqualTo(expectedDetailError);
        });
    }
}
