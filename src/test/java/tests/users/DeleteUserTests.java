package tests.users;

import allure.Layer;
import io.qameta.allure.Story;
import models.users.login.LoginBodyModel;
import models.users.registration.RegistrationBodyModel;
import models.users.registration.SuccessfulRegistrationResponseModel;
import models.users.update.NotProvidedAuthenticationCredentialsResponseModel;
import org.junit.jupiter.api.*;
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
            String accessToken = api.auth.loginAndGetAccessToken(new LoginBodyModel(username, password));
            api.users.deleteUserAuthorized(accessToken);
        }
    }

    @Test
    @Story("Удаление пользователя из системы")
    @Tag("API")
    @DisplayName("Тест на проверку удаления существующего пользователя")
    public void successfulDeleteUserTest() {
        SuccessfulRegistrationResponseModel registrationResponse
                = api.users.registration(new RegistrationBodyModel(username, password));
        step("Проверка корректности зарегистрированных данных", () -> {
            assertThat(registrationResponse.id()).isGreaterThan(0);
            assertThat(registrationResponse.username()).isEqualTo(username);
        });
    }

    @Test
    @Story("Удаление пользователя из системы")
    @Tag("API")
    @DisplayName("Тест на проверку удаления не авторизованного пользователя")
    public void notProvidedAuthenticationCredentialsDeleteUserNegativeTest() {
        SuccessfulRegistrationResponseModel registrationResponse
                = api.users.registration(new RegistrationBodyModel(username, password));
        step("Проверка корректности зарегистрированных данных", () -> {
            assertThat(registrationResponse.id()).isGreaterThan(0);
            assertThat(registrationResponse.username()).isEqualTo(username);
        });

        NotProvidedAuthenticationCredentialsResponseModel deleteResponse
                = api.users.deleteUserUnauthorized();
        step("Проверка текста ошибки в ответе", () -> {
            String actualDetail = deleteResponse.detail();
            String expectedDetail = "Authentication credentials were not provided.";
            assertThat(actualDetail).isEqualTo(expectedDetail);
        });
    }
}
