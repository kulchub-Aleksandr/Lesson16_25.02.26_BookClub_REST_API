package tests;

import models.login.LoginBodyModel;
import models.registration.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

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
        step("Регистрация нового пользователя и проверка ответа (201)", () -> {
            RegistrationBodyModel registrationData = new RegistrationBodyModel(username, password);

            SuccessfulRegistrationResponseModel registrationResponse =
                    api.users.registration(registrationData);

            assertThat(registrationResponse.id()).isGreaterThan(0);
            assertThat(registrationResponse.username()).isEqualTo(username);
            assertThat(registrationResponse.firstName()).isEqualTo("");
            assertThat(registrationResponse.lastName()).isEqualTo("");
            assertThat(registrationResponse.email()).isEqualTo("");

            String ipAddrRegexp = "^((25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)\\.){3}"
                    + "(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)$";
            assertThat(registrationResponse.remoteAddr()).matches(ipAddrRegexp);
        });

        LoginBodyModel loginData = new LoginBodyModel(username, password);
        String accessToken = api.auth.loginAndGetAccessToken(loginData);
        api.users.deleteUserAuthorized(accessToken);


    }

    @Test
    @DisplayName("Тест на проверку регистрации пользователя с уже существующими регистрационными данными")
    public void existingUserRegistrationNegativeTest() {

        RegistrationBodyModel registrationData = new RegistrationBodyModel(username, password);

        step("Регистрация нового пользователя", () -> {
            SuccessfulRegistrationResponseModel registrationResponse_1 =
                    api.users.registration(registrationData);

            assertThat(registrationResponse_1.username()).isEqualTo(username);
        });

        step("Регистрация нового пользователя с уже существующими регистрационными данными и проверка ответа (400)", () -> {

            ExistingUserResponseModel registrationResponse_2 =
                    api.users.registrationExistingUse(registrationData);

            String expectedError = "A user with that username already exists.";
            String actualError = registrationResponse_2.username().getFirst();
            assertThat(actualError).isEqualTo(expectedError);
        });

        LoginBodyModel loginData = new LoginBodyModel(username, password);
        String accessToken = api.auth.loginAndGetAccessToken(loginData);
        api.users.deleteUserAuthorized(accessToken);


    }

    @Test
    @DisplayName("Тест на проверку регистрации пользователя с невалидными регистрационными данными")
    public void invalidUserNameRegistrationNegativeTest() {

        RegistrationBodyModel registrationData = new RegistrationBodyModel(WrongUsername, password);
        step("Регистрация нового пользователя с невалидными регистрационными данными и проверка ответа (400)", () -> {
            InvalidUserNameResponseModel registrationResponse =
                    api.users.registrationInvalidUserName(registrationData);

            String expectedError = "Enter a valid username. This value may contain only letters, numbers, and @/./+/-/_ characters.";
            String actualError = registrationResponse.username().getFirst();
            assertThat(actualError).isEqualTo(expectedError);
        });

    }

    @Test
    @DisplayName("Тест на проверку регистрации пользователя с неподдерживаемым типом передаваемых данных")
    public void unsupportedMediaTypeRegistrationNegativeTest() {

        RegistrationBodyModel registrationData = new RegistrationBodyModel(username, password);
        step("Регистрация нового пользователя с неподдерживаемым типом передаваемых данных и проверка ответа (415)", () -> {
            UnsupportedMediaTypeResponseModel registrationResponse =
                    api.users.registrationUnsupportedMediaType(registrationData);

            String expectedError = "Unsupported media type \"text/plain; charset=ISO-8859-1\" in request.";
            String actualError = registrationResponse.detail();
            assertThat(actualError).isEqualTo(expectedError);
        });
    }
}
