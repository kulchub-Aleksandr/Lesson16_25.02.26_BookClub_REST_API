package tests.users;

import models.users.login.LoginBodyModel;
import models.users.registration.RegistrationBodyModel;
import models.users.registration.SuccessfulRegistrationResponseModel;
import models.users.update.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import tests.TestBase;
import tests.TestData;

import static io.qameta.allure.Allure.step;
import static org.assertj.core.api.Assertions.assertThat;

public class UpdateUserTests extends TestBase {

    private final TestData testData = new TestData();
    private String username;
    private String password;
    private String firstName;
    private String lastName;
    private String email;

    @BeforeEach
    public void prepareTestData() {
        username = testData.getUsername();
        password = testData.getPassword();
        firstName = testData.getFirstName();
        lastName = testData.getLastName();
        email = testData.getEmail();
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
    @DisplayName("Тест на проверку изменения всех данных пользователя методом PUT")
    public void successfulUpdateUserTest() {

        SuccessfulRegistrationResponseModel registrationResponse_1 =
                step("Регистрация нового пользователя", () -> {
                    RegistrationBodyModel registrationData = new RegistrationBodyModel(username, password);
                    return api.users.registration(registrationData);
                });

        step("Проверка соответствия отправленных данных с данными в ответе", () -> {
            assertThat(registrationResponse_1.username()).isEqualTo(username);
        });

        String actualAccessToken = step("Авторизация и получение access-токена", () -> {
            LoginBodyModel loginData = new LoginBodyModel(username, password);
            return api.auth.loginAndGetAccessToken(loginData);
        });

        SuccessfulUpdateUserResponseModel updateResponse =
                step("Отправка запроса put с access-токеном и проверка ответа (200)", () -> {
                    UpdateBodyModel updateData = new UpdateBodyModel(
                            username,
                            firstName,
                            lastName,
                            email);
                    return api.users.update(actualAccessToken, updateData);
                });
        step("Проверка корректности зарегистрированных данных", () -> {
            assertThat(updateResponse.username()).isEqualTo(username);
            assertThat(updateResponse.firstName()).isEqualTo(firstName);
            assertThat(updateResponse.lastName()).isEqualTo(lastName);
            assertThat(updateResponse.email()).isEqualTo(email);

        });

        SuccessfulUpdateUserResponseModel updatedUserData =
                step("Проверка изменений методом get и проверка ответа (200)", () ->
                        api.users.updateCheck(actualAccessToken));
        step("Подтверждение изменений через GET‑запрос", () -> {
            assertThat(updatedUserData.username()).isEqualTo(username);
            assertThat(updatedUserData.firstName()).isEqualTo(firstName);
            assertThat(updatedUserData.lastName()).isEqualTo(lastName);
            assertThat(updatedUserData.email()).isEqualTo(email);
        });

    }

    @Test
    @DisplayName("Тест на проверку изменения всех данных пользователя методом PUT без предварительной аутентификации")
    public void notProvidedAuthenticationCredentialsUpdateUserNegativeTest() {

        SuccessfulRegistrationResponseModel registrationResponse
                = step("Регистрация нового пользователя и проверка ответа (201)", () -> {
            RegistrationBodyModel registrationData = new RegistrationBodyModel(username, password);
            return api.users.registration(registrationData);
        });
        step("Проверка корректности зарегистрированных данных", () -> {
            assertThat(registrationResponse.id()).isGreaterThan(0);
            assertThat(registrationResponse.username()).isEqualTo(username);
        });


        NotProvidedAuthenticationCredentialsResponseModel updateResponse =
                step("Отправка запроса put без предварительной аутентификации и проверка ответа (401)", () -> {
                    UpdateBodyModel updateData = new UpdateBodyModel(
                            username,
                            firstName,
                            lastName,
                            email);
                    return api.users.updateNotProvidedAuthenticationCredentials(updateData);
                });
        step("Проверка текста ошибки в ответе", () -> {
            String actualDetail = updateResponse.detail();
            String expectedDetail = "Authentication credentials were not provided.";
            assertThat(actualDetail).isEqualTo(expectedDetail);
        });


    }

    @Test
    @DisplayName("Тест на проверку изменения выборочных данных пользователя методом PATCH")
    public void partialUpdateUserTest() {

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

        SuccessfulUpdateUserResponseModel updateResponse =
                step("Отправка запроса patch с access-токеном и проверка ответа (200)", () -> {
                    PartialUpdateBodyModel updateData = new PartialUpdateBodyModel(
                            firstName,
                            lastName);
                    return api.users.updateWithPatch(accessToken, updateData);
                });
        step("Проверка корректности полученных данных", () -> {
            assertThat(updateResponse.id()).isEqualTo(registrationResponse.id());
            assertThat(updateResponse.username()).isEqualTo(username);
            assertThat(updateResponse.firstName()).isEqualTo(firstName);
            assertThat(updateResponse.lastName()).isEqualTo(lastName);
            assertThat(updateResponse.email()).isEqualTo("");

            String registrationIpAddress = registrationResponse.remoteAddr();
            String updateIpAddress = updateResponse.remoteAddr();
            assertThat(registrationIpAddress).isEqualTo(updateIpAddress);
        });

        SuccessfulUpdateUserResponseModel updatedUserData =
                step("Проверка изменений методом get и проверка ответа (200)", () ->
                        api.users.updateCheck(accessToken));
        step("Подтверждение изменений через GET‑запрос", () -> {
            assertThat(updatedUserData.id()).isEqualTo(registrationResponse.id());
            assertThat(updatedUserData.username()).isEqualTo(username);
            assertThat(updatedUserData.firstName()).isEqualTo(firstName);
            assertThat(updatedUserData.lastName()).isEqualTo(lastName);
            assertThat(updatedUserData.email()).isEqualTo("");
        });


    }

    @Test
    @DisplayName("Тест на проверку изменения выборочных данных пользователя методом PUT")
    public void partialUpdateUserWithPutMethodNegativeTest() {

        SuccessfulRegistrationResponseModel registrationResponse
                = step("Регистрация нового пользователя и проверка ответа (201)", () -> {
            RegistrationBodyModel registrationData = new RegistrationBodyModel(username, password);
            return api.users.registration(registrationData);
        });
        step("Проверка корректности зарегистрированных данных", () -> {
            assertThat(registrationResponse.id()).isGreaterThan(0);
            assertThat(registrationResponse.username()).isEqualTo(username);
        });

        String actualAccessToken = step("Авторизация и получение access-токена", () -> {
            LoginBodyModel loginData = new LoginBodyModel(username, password);
            return api.auth.loginAndGetAccessToken(loginData);
        });

        PartialWithPutMethodUpdateUserResponseModel updateResponse =
                step("Отправка запроса put с access-токеном и проверка ответа (400)", () -> {
                    PartialUpdateBodyModel updateData = new PartialUpdateBodyModel(firstName, lastName);
                    return api.users.updatePartialWithPut(actualAccessToken, updateData);
                });
        step("Проверка текста ошибки в ответе", () -> {
            String actualUsername = updateResponse.username().getFirst();
            String expectedUsername = "This field is required.";
            String actualEmail = updateResponse.email().getFirst();
            String expectedEmail = "This field is required.";

            assertThat(actualUsername).isEqualTo(expectedUsername);
            assertThat(actualEmail).isEqualTo(expectedEmail);
        });
    }
}
