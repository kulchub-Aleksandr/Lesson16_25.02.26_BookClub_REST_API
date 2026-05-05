package tests.users;

import allure.Layer;
import io.qameta.allure.Story;
import models.users.login.*;
import models.users.registration.RegistrationBodyModel;
import models.users.registration.SuccessfulRegistrationResponseModel;
import org.junit.jupiter.api.*;
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
    String expectedTokenPath;

    @BeforeEach
    public void prepareTestData() {
        username = testData.getUsername();
        password = testData.getPassword();
        wrongPassword = password + "1234";
        wrongUsername = username + "qa";
        emptyPassword = testData.getEmptyPassword();
        emptyUsername = testData.getEmptyUsername();
        expectedTokenPath = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9";
    }

    @AfterEach
    void cleanUpTestUsers() {
        if (username != null && password != null) {
            String accessToken = api.auth.loginAndGetAccessToken(new LoginBodyModel(username, password));
            api.users.deleteUserAuthorized(accessToken);
        }
    }

    @Test
    @Story("Авторизация пользователя в системе")
    @Tag("API")
    @DisplayName("Тест на проверку авторизации существующего пользователя")
    public void successfulLoginTest() {

        SuccessfulRegistrationResponseModel registrationResponse
                = api.users.registration(new RegistrationBodyModel(username, password));
        step("Проверка корректности зарегистрированных данных", () -> {
            assertThat(registrationResponse.id()).isGreaterThan(0);
            assertThat(registrationResponse.username()).isEqualTo(username);
        });

        SuccessfulLoginResponseModel loginResponse
                = api.auth.login(new LoginBodyModel(username, password));

        step("Проверка корректности полученных токенов", () -> {

            String actualAccess = loginResponse.access();
            String actualRefresh = loginResponse.refresh();

            assertThat(actualAccess).startsWith(expectedTokenPath);
            assertThat(actualRefresh).startsWith(expectedTokenPath);
            assertThat(actualAccess).isNotEqualTo(actualRefresh);
        });
    }

    @Test
    @Story("Авторизация пользователя в системе")
    @Tag("API")
    @DisplayName("Тест на проверку авторизации пользователя с применением не правильного пароля")
    public void wrongPasswordLoginNegativeTest() {

        SuccessfulRegistrationResponseModel registrationResponse
                = api.users.registration(new RegistrationBodyModel(username, password));
        step("Проверка корректности зарегистрированных данных", () -> {
            assertThat(registrationResponse.id()).isGreaterThan(0);
            assertThat(registrationResponse.username()).isEqualTo(username);
        });

        WrongCredentialsLoginResponseModel loginResponse
                = api.auth.loginWrongCredentials(new LoginBodyModel(username, wrongPassword));

        step("Проверка текста ошибки в ответе", () -> {
            String expectedDetailError = "Invalid username or password.";
            String actualDetailError = loginResponse.detail();
            assertThat(actualDetailError).isEqualTo(expectedDetailError);
        });
    }

    @Test
    @Story("Авторизация пользователя в системе")
    @Tag("API")
    @DisplayName("Тест на проверку авторизации пользователя с применением пустого поля для пароля")
    public void emptyPasswordLoginNegativeTest() {
        SuccessfulRegistrationResponseModel registrationResponse
                = api.users.registration(new RegistrationBodyModel(username, password));
        step("Проверка корректности зарегистрированных данных", () -> {
            assertThat(registrationResponse.id()).isGreaterThan(0);
            assertThat(registrationResponse.username()).isEqualTo(username);
        });

        EmptyPasswordResponseModel loginResponse
                = api.auth.loginEmptyPassword(new LoginBodyModel(username, emptyPassword));

        step("Проверка текста ошибки в ответе", () -> {
            String expectedDetailError = "This field may not be blank.";
            String actualDetailError = loginResponse.password().getFirst();

            assertThat(actualDetailError).isEqualTo(expectedDetailError);
        });
    }

    @Test
    @Story("Авторизация пользователя в системе")
    @Tag("API")
    @DisplayName("Тест на проверку авторизации пользователя с применением не правильного логина")
    public void wrongUserNameLoginNegativeTest() {
        SuccessfulRegistrationResponseModel registrationResponse
                = api.users.registration(new RegistrationBodyModel(username, password));
        step("Проверка корректности зарегистрированных данных", () -> {
            assertThat(registrationResponse.id()).isGreaterThan(0);
            assertThat(registrationResponse.username()).isEqualTo(username);
        });

        WrongCredentialsLoginResponseModel loginResponse
                = api.auth.loginWrongCredentials(new LoginBodyModel(wrongUsername, password));

        step("Проверка текста ошибки в ответе", () -> {
            String expectedDetailError = "Invalid username or password.";
            String actualDetailError = loginResponse.detail();
            assertThat(actualDetailError).isEqualTo(expectedDetailError);
        });
    }

    @Test
    @Story("Авторизация пользователя в системе")
    @Tag("API")
    @DisplayName("Тест на проверку авторизации пользователя с применением пустого поля для логина")
    public void emptyUserNameLoginNegativeTest() {
        SuccessfulRegistrationResponseModel registrationResponse
                = api.users.registration(new RegistrationBodyModel(username, password));
        step("Проверка корректности зарегистрированных данных", () -> {
            assertThat(registrationResponse.id()).isGreaterThan(0);
            assertThat(registrationResponse.username()).isEqualTo(username);
        });

        EmptyUserResponseModel loginResponse =
                api.auth.loginEmptyUser(new LoginBodyModel(emptyUsername, password));

        step("Проверка текста ошибки в ответе", () -> {
            String expectedDetailError = "This field may not be blank.";
            String actualDetailError = loginResponse.username().getFirst();

            assertThat(actualDetailError).isEqualTo(expectedDetailError);
        });
    }

    @Test
    @Story("Авторизация пользователя в системе")
    @Tag("API")
    @DisplayName("Тест на проверку авторизации пользователя с применением пустого поля для логина и пустого поля для пароля")
    public void emptyUserNameEmptyPasswordLoginNegativeTest() {
        SuccessfulRegistrationResponseModel registrationResponse
                = api.users.registration(new RegistrationBodyModel(username, password));
        step("Проверка корректности зарегистрированных данных", () -> {
            assertThat(registrationResponse.id()).isGreaterThan(0);
            assertThat(registrationResponse.username()).isEqualTo(username);
        });

        EmptyUserEmptyPasswordResponseModel loginResponse
                = api.auth.loginEmptyUserEmptyPassword(new LoginBodyModel(emptyUsername, emptyPassword));

        step("Проверка текста ошибки в ответе", () ->
        {
            String expectedDetailError = "This field may not be blank.";
            String actualDetailError_1 = loginResponse.username().getFirst();
            String actualDetailError_2 = loginResponse.password().getFirst();

            assertThat(actualDetailError_1).isEqualTo(expectedDetailError);
            assertThat(actualDetailError_2).isEqualTo(expectedDetailError);
        });
    }
}
