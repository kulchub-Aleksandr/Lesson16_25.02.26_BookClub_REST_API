package tests;

import models.login.LoginBodyModel;
import models.registration.RegistrationBodyModel;
import models.registration.SuccessfulRegistrationResponseModel;
import models.update.NotProvidedAuthenticationCredentialsResponseModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static allure.CustomAllureListener.withCustomTemplate;
import static io.qameta.allure.Allure.step;
import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static specs.login.LoginSpec.loginRequestSpec;
import static specs.login.LoginSpec.successfulLoginResponseSpec;
import static specs.registration.RegistrationSpec.registrationRequestSpec;
import static specs.registration.RegistrationSpec.successfulRegistrationResponseSpec;
import static specs.update.UpdateSpec.notProvidedAuthenticationCredentialsResponseSpec;

public class DeleteUserTests extends TestBase {
    private final TestData testData = new TestData();
    private String username;
    private String password;

    @BeforeEach
    public void prepareTestData() {
        username = testData.getUsername();
        password = testData.getPassword();
    }

    @Test
    @DisplayName("Тест на проверку удаления существующего пользователя")
    public void successfulDeleteUserTest() {
        step("Регистрация нового пользователя", () -> {
            RegistrationBodyModel registrationData = new RegistrationBodyModel(username, password);
            SuccessfulRegistrationResponseModel registrationResponse = given(registrationRequestSpec)
                    .body(registrationData)
                    .when()
                    .post("/users/register/")
                    .then()
                    .spec(successfulRegistrationResponseSpec)
                    .extract()
                    .as(SuccessfulRegistrationResponseModel.class);

            assertThat(registrationResponse.id()).isGreaterThan(0);
            assertThat(registrationResponse.username()).isEqualTo(username);

        });

        LoginBodyModel loginData = new LoginBodyModel(username, password);

        String accessToken = step("Авторизация и получение токена для удаления пользователя ", () ->
                given(loginRequestSpec)
                        .body(loginData)
                        .when()
                        .post("/auth/token/")
                        .then()
                        .spec(successfulLoginResponseSpec)
                        .extract()
                        .path("access"));

        step("Удаление пользователя и проверка ответа (204)", () -> {
            given()
                    .filter(withCustomTemplate())
                    .log().all()
                    .header("Authorization", "Bearer " + accessToken)
                    .when()
                    .delete("/users/me/")
                    .then()
                    .log().all()
                    .statusCode(204);
        });
    }

    @Test
    @DisplayName("Тест на проверку удаления не авторизованного пользователя")
    public void notProvidedAuthenticationCredentialsDeleteUserNegativeTest() {
        step("Регистрация нового пользователя", () -> {
            RegistrationBodyModel registrationData = new RegistrationBodyModel(username, password);

            SuccessfulRegistrationResponseModel registrationResponse = given(registrationRequestSpec)
                    .body(registrationData)
                    .when()
                    .post("/users/register/")
                    .then()
                    .spec(successfulRegistrationResponseSpec)
                    .extract()
                    .as(SuccessfulRegistrationResponseModel.class);

            assertThat(registrationResponse.id()).isGreaterThan(0);
            assertThat(registrationResponse.username()).isEqualTo(username);

        });

        NotProvidedAuthenticationCredentialsResponseModel deleteResponse =
                step("Удаление пользователя без авторизации и проверка ответа (401)", () -> {
                    return given()
                            .filter(withCustomTemplate())
                            .log().all()
                            .when()
                            .delete("/users/me/")
                            .then()
                            .spec(notProvidedAuthenticationCredentialsResponseSpec)
                            .extract()
                            .as(NotProvidedAuthenticationCredentialsResponseModel.class);
                });
        step("Проверка текста ошибки в ответе", () -> {
            String actualDetail = deleteResponse.detail();
            String expectedDetail = "Authentication credentials were not provided.";
            assertThat(actualDetail).isEqualTo(expectedDetail);
        });

        LoginBodyModel loginData = new LoginBodyModel(username, password);
        String accessToken = step("Авторизация и получение токена для удаления временного пользователя ", () ->
                given(loginRequestSpec)
                        .body(loginData)
                        .when()
                        .post("/auth/token/")
                        .then()
                        .spec(successfulLoginResponseSpec)
                        .extract()
                        .path("access"));

        step("Удаление пользователя", () -> {
            given()
                    .filter(withCustomTemplate())
                    .log().all()
                    .header("Authorization", "Bearer " + accessToken)
                    .when()
                    .delete("/users/me/")
                    .then()
                    .log().all()
                    .statusCode(204);
        });
    }
}
