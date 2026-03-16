package tests.users;

import models.users.login.LoginBodyModel;
import models.users.registration.RegistrationBodyModel;
import models.users.registration.SuccessfulRegistrationResponseModel;
import models.users.update.NotProvidedAuthenticationCredentialsResponseModel;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import tests.TestBase;
import tests.TestData;

import static io.qameta.allure.Allure.step;
import static org.assertj.core.api.Assertions.assertThat;

public class DeleteUserTests extends TestBase {
    private final TestData testData = new TestData();
    private String username;
    private String password;

    @BeforeEach
    public void prepareTestData() {
        username = testData.getUsername();
        password = testData.getPassword();
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
    @DisplayName("Тест на проверку удаления существующего пользователя")
    public void successfulDeleteUserTest() {

        RegistrationBodyModel registrationData = new RegistrationBodyModel(username, password);

        SuccessfulRegistrationResponseModel registrationResponse =
                api.users.registration(registrationData);

        assertThat(registrationResponse.id()).isGreaterThan(0);
        assertThat(registrationResponse.username()).isEqualTo(username);

//        LoginBodyModel loginData = new LoginBodyModel(username, password);
//
//        String accessToken = api.auth.loginAndGetAccessToken(loginData);
//        api.users.deleteUserAuthorized(accessToken);
    }

    @Test
    @DisplayName("Тест на проверку удаления не авторизованного пользователя")
    public void notProvidedAuthenticationCredentialsDeleteUserNegativeTest() {

        RegistrationBodyModel registrationData = new RegistrationBodyModel(username, password);

        SuccessfulRegistrationResponseModel registrationResponse =
                api.users.registration(registrationData);

        assertThat(registrationResponse.id()).isGreaterThan(0);
        assertThat(registrationResponse.username()).isEqualTo(username);


        step("Удаление пользователя без авторизации и проверка ответа (401)", () -> {
            NotProvidedAuthenticationCredentialsResponseModel deleteResponse =
                    api.users.deleteUserUnauthorized();

            String actualDetail = deleteResponse.detail();
            String expectedDetail = "Authentication credentials were not provided.";
            assertThat(actualDetail).isEqualTo(expectedDetail);
        });
    }
}
