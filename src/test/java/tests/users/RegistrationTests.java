package tests.users;

import io.qameta.allure.Story;
import models.users.login.LoginBodyModel;
import models.users.registration.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
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
    @Story("Регистрация нового пользователя")
    @Tag("API")
    @DisplayName("Тест на проверку регистрации нового пользователя")
    public void successfulRegistrationTest() {

        SuccessfulRegistrationResponseModel registrationResponse
                = api.users.registration(new RegistrationBodyModel(username, password));

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

        String accessToken = api.auth.loginAndGetAccessToken(new LoginBodyModel(username, password));
        api.users.deleteUserAuthorized(accessToken);
    }

    @Test
    @Story("Регистрация нового пользователя")
    @Tag("API")
    @DisplayName("Тест на проверку регистрации пользователя с уже существующими регистрационными данными")
    public void existingUserRegistrationNegativeTest() {

        SuccessfulRegistrationResponseModel registrationResponse_1
                = api.users.registration(new RegistrationBodyModel(username, password));

        step("Проверка соответствия отправленных данных с данными в ответе", () -> {
            assertThat(registrationResponse_1.username()).isEqualTo(username);
        });

        ExistingUserResponseModel registrationResponse_2
                = api.users.registrationExistingUse(new RegistrationBodyModel(username, password));
        step("Проверка текста ошибки в ответе", () -> {
            String expectedError = "A user with that username already exists.";
            String actualError = registrationResponse_2.username().getFirst();
            assertThat(actualError).isEqualTo(expectedError);
        });

        String accessToken = api.auth.loginAndGetAccessToken(new LoginBodyModel(username, password));
        api.users.deleteUserAuthorized(accessToken);
    }

    @Test
    @Story("Регистрация нового пользователя")
    @Tag("API")
    @DisplayName("Тест на проверку регистрации пользователя с невалидными регистрационными данными")
    public void invalidUserNameRegistrationNegativeTest() {

        InvalidUserNameResponseModel registrationResponse
                = api.users.registrationInvalidUserName(new RegistrationBodyModel(WrongUsername, password));

        step("Проверка текста ошибки в ответе", () -> {
            String expectedError = "Enter a valid username. This value may contain only letters, numbers, and @/./+/-/_ characters.";
            String actualError = registrationResponse.username().getFirst();
            assertThat(actualError).isEqualTo(expectedError);
        });
    }

    @Test
    @Story("Регистрация нового пользователя")
    @Tag("API")
    @DisplayName("Тест на проверку регистрации пользователя с неподдерживаемым типом передаваемых данных")
    public void unsupportedMediaTypeRegistrationNegativeTest() {

        UnsupportedMediaTypeResponseModel registrationResponse
                = api.users.registrationUnsupportedMediaType(new RegistrationBodyModel(username, password));

        step("Проверка текста ошибки в ответе", () -> {
            String expectedError = "Unsupported media type \"text/plain; charset=ISO-8859-1\" in request.";
            String actualError = registrationResponse.detail();
            assertThat(actualError).isEqualTo(expectedError);
        });
    }
}
