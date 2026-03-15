package tests;

import models.login.LoginBodyModel;
import models.registration.RegistrationBodyModel;
import models.registration.SuccessfulRegistrationResponseModel;
import models.update.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

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
        step("Регистрация нового пользователя", () -> {
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

        String actualAccessToken = api.auth.loginAndGetAccessToken(loginData);

        UpdateBodyModel updateData = new UpdateBodyModel(username,
                firstName, lastName, email);

        step("Отправка запроса put с access-токеном и проверка ответа (200)", () -> {
            SuccessfulUpdateUserResponseModel updateResponse =
                    api.users.update(actualAccessToken, updateData);

            assertThat(updateResponse.username()).isEqualTo(username);
            assertThat(updateResponse.firstName()).isEqualTo(firstName);
            assertThat(updateResponse.lastName()).isEqualTo(lastName);
            assertThat(updateResponse.email()).isEqualTo(email);

        });

        step("Проверка изменений методом get и проверка ответа (200)", () -> {
            SuccessfulUpdateUserResponseModel updatedUserData =
                    api.users.updateCheck(actualAccessToken);

            assertThat(updatedUserData.username()).isEqualTo(username);
            assertThat(updatedUserData.firstName()).isEqualTo(firstName);
            assertThat(updatedUserData.lastName()).isEqualTo(lastName);
            assertThat(updatedUserData.email()).isEqualTo(email);
        });


    }

    @Test
    @DisplayName("Тест на проверку изменения всех данных пользователя методом PUT без предварительной аутентификации")
    public void notProvidedAuthenticationCredentialsUpdateUserNegativeTest() {

        step("Регистрация нового пользователя", () -> {
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

            //String registrationIpAddress = registrationResponse.remoteAddr();
        });

        UpdateBodyModel updateData = new UpdateBodyModel(username,
                firstName, lastName, email);
        step("Отправка запроса put без предварительной аутентификации и проверка ответа (401)", () -> {
            NotProvidedAuthenticationCredentialsResponseModel updateResponse =
                    api.users.updateNotProvidedAuthenticationCredentials(updateData);

            String actualDetail = updateResponse.detail();
            String expectedDetail = "Authentication credentials were not provided.";
            assertThat(actualDetail).isEqualTo(expectedDetail);
        });
    }

    @Test
    @DisplayName("Тест на проверку изменения выборочных данных пользователя методом PATCH")
    public void partialUpdateUserTest() {


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

        String registrationIpAddress = registrationResponse.remoteAddr();


        LoginBodyModel loginData = new LoginBodyModel(username, password);

        String actualAccessToken = api.auth.loginAndGetAccessToken(loginData);

        PartialUpdateBodyModel updateData = new PartialUpdateBodyModel(firstName, lastName);

        step("Отправка запроса patch с access-токеном и проверка ответа (200)", () -> {
            SuccessfulUpdateUserResponseModel updateResponse =
                    api.users.updateWithPatch(actualAccessToken, updateData);

            assertThat(updateResponse.id()).isEqualTo(registrationResponse.id());
            assertThat(updateResponse.username()).isEqualTo(username);
            assertThat(updateResponse.firstName()).isEqualTo(firstName);
            assertThat(updateResponse.lastName()).isEqualTo(lastName);
            assertThat(updateResponse.email()).isEqualTo("");
            assertThat(registrationResponse.remoteAddr()).matches(ipAddrRegexp);

            String updateIpAddress = updateResponse.remoteAddr();
            assertThat(registrationIpAddress).isEqualTo(updateIpAddress);
        });


        SuccessfulUpdateUserResponseModel updatedUserData =
                api.users.updateCheck(actualAccessToken);

        assertThat(updatedUserData.id()).isEqualTo(registrationResponse.id());
        assertThat(updatedUserData.username()).isEqualTo(username);
        assertThat(updatedUserData.firstName()).isEqualTo(firstName);
        assertThat(updatedUserData.lastName()).isEqualTo(lastName);
        assertThat(updatedUserData.email()).isEqualTo("");

    }

    @Test
    @DisplayName("Тест на проверку изменения выборочных данных пользователя методом PUT")
    public void partialUpdateUserWithPutMethodNegativeTest() {

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


        LoginBodyModel loginData = new LoginBodyModel(username, password);

        String actualAccessToken = api.auth.loginAndGetAccessToken(loginData);

        PartialUpdateBodyModel updateData = new PartialUpdateBodyModel(firstName, lastName);
        step("Отправка запроса put с access-токеном и проверка ответа (400)", () -> {
            PartialWithPutMethodUpdateUserResponseModel updateResponse =
                    api.users.updatePartialWithPut(actualAccessToken, updateData);

            String actualUsername = updateResponse.username().getFirst();
            String expectedUsername = "This field is required.";
            String actualEmail = updateResponse.email().getFirst();
            String expectedEmail = "This field is required.";

            assertThat(actualUsername).isEqualTo(expectedUsername);
            assertThat(actualEmail).isEqualTo(expectedEmail);
        });
    }
}
