package tests.users;

import models.users.login.LoginBodyModel;
import models.users.registration.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import tests.TestBase;
import tests.TestData;

import static io.qameta.allure.Allure.step;
import static org.assertj.core.api.Assertions.assertThat;

public class RegistrationTests extends TestBase {
    private final TestData testData = new TestData();
    private String username;
    private String password;
    private String WrongUsername;

    @BeforeEach
    public void prepareTestData() {
        username = testData.getUsername();
        password = testData.getPassword();
        WrongUsername = testData.getWrongUsername();
    }

    @Test
    @DisplayName("Тест на проверку регистрации нового пользователя")
    public void successfulRegistrationTest() {

        SuccessfulRegistrationResponseModel registrationResponse
                = step("Регистрация нового пользователя и проверка ответа (201)", () -> {
            RegistrationBodyModel registrationData = new RegistrationBodyModel(username, password);
            return api.users.registration(registrationData);
        });
        step("Проверка корректности зарегистрированных данных", () -> {
            assertThat(registrationResponse.id()).isGreaterThan(0);
            assertThat(registrationResponse.username()).isEqualTo(username);
            assertThat(registrationResponse.firstName()).isEqualTo("");
            assertThat(registrationResponse.lastName()).isEqualTo("");
            assertThat(registrationResponse.email()).isEqualTo("");

            String ipAddrRegexp = "^((25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)\\.){3}"
                    + "(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)$";
            assertThat(registrationResponse.remoteAddr()).matches(ipAddrRegexp);
        });

        String accessToken = step("Авторизация и получение access-токена для удаления пользователя", () -> {
            LoginBodyModel loginData = new LoginBodyModel(username, password);
            return api.auth.loginAndGetAccessToken(loginData);
        });
        api.users.deleteUserAuthorized(accessToken);
    }

    @Test
    @DisplayName("Тест на проверку регистрации пользователя с уже существующими регистрационными данными")
    public void existingUserRegistrationNegativeTest() {

        SuccessfulRegistrationResponseModel registrationResponse_1 =
                step("Регистрация нового пользователя", () -> {
                    RegistrationBodyModel registrationData = new RegistrationBodyModel(username, password);
                    return api.users.registration(registrationData);
                });

        step("Проверка соответствия отправленных данных с данными в ответе", () -> {
            assertThat(registrationResponse_1.username()).isEqualTo(username);
        });

        ExistingUserResponseModel registrationResponse_2 =
                step("Регистрация нового пользователя с уже существующими регистрационными данными и проверка ответа (400)", () -> {
                    RegistrationBodyModel registrationData = new RegistrationBodyModel(username, password);

                    return api.users.registrationExistingUse(registrationData);
                });
        step("Проверка текста ошибки в ответе", () -> {
            String expectedError = "A user with that username already exists.";
            String actualError = registrationResponse_2.username().getFirst();
            assertThat(actualError).isEqualTo(expectedError);
        });

        String accessToken = step("Авторизация и получение access-токена для удаления пользователя", () -> {
            LoginBodyModel loginData = new LoginBodyModel(username, password);
            return api.auth.loginAndGetAccessToken(loginData);
        });
        api.users.deleteUserAuthorized(accessToken);
    }

    @Test
    @DisplayName("Тест на проверку регистрации пользователя с невалидными регистрационными данными")
    public void invalidUserNameRegistrationNegativeTest() {

        InvalidUserNameResponseModel registrationResponse =
                step("Регистрация нового пользователя с невалидными регистрационными данными и проверка ответа (400)", () -> {
                    RegistrationBodyModel registrationData = new RegistrationBodyModel(WrongUsername, password);
                    return api.users.registrationInvalidUserName(registrationData);
                });
        step("Проверка текста ошибки в ответе", () -> {
            String expectedError = "Enter a valid username. This value may contain only letters, numbers, and @/./+/-/_ characters.";
            String actualError = registrationResponse.username().getFirst();
            assertThat(actualError).isEqualTo(expectedError);
        });
    }

    @Test
    @DisplayName("Тест на проверку регистрации пользователя с неподдерживаемым типом передаваемых данных")
    public void unsupportedMediaTypeRegistrationNegativeTest() {

        UnsupportedMediaTypeResponseModel registrationResponse =
                step("Регистрация нового пользователя с неподдерживаемым типом передаваемых данных и проверка ответа (415)", () -> {
                    RegistrationBodyModel registrationData = new RegistrationBodyModel(username, password);
                    return api.users.registrationUnsupportedMediaType(registrationData);
                });

        step("Проверка текста ошибки в ответе", () -> {
            String expectedError = "Unsupported media type \"text/plain; charset=ISO-8859-1\" in request.";
            String actualError = registrationResponse.detail();
            assertThat(actualError).isEqualTo(expectedError);
        });
    }
}
