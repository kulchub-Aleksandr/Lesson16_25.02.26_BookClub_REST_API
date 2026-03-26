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
            String accessToken = api.auth.loginAndGetAccessToken(new LoginBodyModel(username, password));
            api.users.deleteUserAuthorized(accessToken);
        }
    }

    @Test
    @DisplayName("Тест на проверку изменения всех данных пользователя методом PUT")
    public void successfulUpdateUserTest() {

        SuccessfulRegistrationResponseModel registrationResponse
                = api.users.registration(new RegistrationBodyModel(username, password));
        step("Проверка соответствия отправленных данных с данными в ответе", () -> {
            assertThat(registrationResponse.username()).isEqualTo(username);
        });

        String actualAccessToken = api.auth.loginAndGetAccessToken(new LoginBodyModel(username, password));

        SuccessfulUpdateUserResponseModel updateResponse
                = api.users.updateUser(actualAccessToken, new UpdateBodyModel(
                            username,
                            firstName,
                            lastName,
                            email));

        step("Проверка корректности зарегистрированных данных", () -> {
            assertThat(updateResponse.username()).isEqualTo(username);
            assertThat(updateResponse.firstName()).isEqualTo(firstName);
            assertThat(updateResponse.lastName()).isEqualTo(lastName);
            assertThat(updateResponse.email()).isEqualTo(email);

        });

        SuccessfulUpdateUserResponseModel updatedUserData
                = api.users.getUserData(actualAccessToken);
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
                = api.users.registration(new RegistrationBodyModel(username, password));
        step("Проверка корректности зарегистрированных данных", () -> {
            assertThat(registrationResponse.username()).isEqualTo(username);
        });

        NotProvidedAuthenticationCredentialsResponseModel updateResponse
                = api.users.updateNotProvidedAuthenticationCredentials(new UpdateBodyModel(
                            username,
                            firstName,
                            lastName,
                            email));

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
                = api.users.registration(new RegistrationBodyModel(username, password));
        step("Проверка корректности зарегистрированных данных", () -> {
            assertThat(registrationResponse.id()).isGreaterThan(0);
            assertThat(registrationResponse.username()).isEqualTo(username);//
        });

        String accessToken = api.auth.loginAndGetAccessToken(new LoginBodyModel(username, password));

        SuccessfulUpdateUserResponseModel updateResponse
                = api.users.updateWithPatch(accessToken, new PartialUpdateBodyModel(
                            firstName,
                            lastName));

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

        SuccessfulUpdateUserResponseModel updatedUserData
                = api.users.getUserData(accessToken);
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
                = api.users.registration(new RegistrationBodyModel(username, password));
        step("Проверка корректности зарегистрированных данных", () -> {
            assertThat(registrationResponse.id()).isGreaterThan(0);
            assertThat(registrationResponse.username()).isEqualTo(username);
        });

        String actualAccessToken = api.auth.loginAndGetAccessToken(new LoginBodyModel(username, password));

        PartialWithPutMethodUpdateUserResponseModel updateResponse
                = api.users.updatePartialWithPut(actualAccessToken, new PartialUpdateBodyModel(
                            firstName, lastName));

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
