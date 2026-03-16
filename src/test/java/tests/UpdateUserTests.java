package tests;

import models.login.LoginBodyModel;
import models.registration.RegistrationBodyModel;
import models.registration.SuccessfulRegistrationResponseModel;
import models.update.*;
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
import static specs.update.UpdateSpec.*;

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

    @Test
    @DisplayName("Тест на проверку изменения всех данных пользователя методом PUT")
    public void successfulUpdateUserTest() {
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
        String actualAccessToken = step("Авторизация и получение access-токена", () ->
                given(loginRequestSpec)
                        .body(loginData)
                        .when()
                        .post("/auth/token/")
                        .then()
                        .spec(successfulLoginResponseSpec)
                        .extract()
                        .path("access"));

        UpdateBodyModel updateData = new UpdateBodyModel(username,
                firstName, lastName, email);

        step("Отправка запроса put с access-токеном и проверка ответа (200)", () -> {
            SuccessfulUpdateUserResponseModel updateResponse = given(updateRequestSpec)
                    .header("Authorization", "Bearer " + actualAccessToken)
                    .body(updateData)
                    .when()
                    .put("/users/me/")
                    .then()
                    .spec(successfulUpdateResponseSpec)
                    .extract()
                    .as(SuccessfulUpdateUserResponseModel.class);
            assertThat(updateResponse.username()).isEqualTo(username);
            assertThat(updateResponse.firstName()).isEqualTo(firstName);
            assertThat(updateResponse.lastName()).isEqualTo(lastName);
            assertThat(updateResponse.email()).isEqualTo(email);

        });

        step("Проверка изменений методом get и проверка ответа (200)", () -> {
            SuccessfulUpdateUserResponseModel updatedUserData = given()
                    .filter(withCustomTemplate())
                    .header("Authorization", "Bearer " + actualAccessToken)
                    .when()
                    .get("/users/me/")
                    .then()
                    .spec(updatedUserDataResponseSpec)
                    .extract()
                    .as(SuccessfulUpdateUserResponseModel.class);

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

        UpdateBodyModel updateData = new UpdateBodyModel(username,
                firstName, lastName, email);
        NotProvidedAuthenticationCredentialsResponseModel updateResponse = step("Отправка запроса put без предварительной аутентификации и проверка ответа (401)", () -> {
            return given(updateRequestSpec)
                    .body(updateData)
                    .when()
                    .put("/users/me/")
                    .then()
                    .spec(notProvidedAuthenticationCredentialsResponseSpec)
                    .extract()
                    .as(NotProvidedAuthenticationCredentialsResponseModel.class);
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
        String actualAccessToken = step("Авторизация и получение access-токена", () ->
                given(loginRequestSpec)
                        .body(loginData)
                        .when()
                        .post("/auth/token/")
                        .then()
                        .spec(successfulLoginResponseSpec)
                        .extract()
                        .path("access"));

        PartialUpdateBodyModel updateData = new PartialUpdateBodyModel(firstName, lastName);

        step("Отправка запроса patch с access-токеном и проверка ответа (200)", () -> {
            SuccessfulUpdateUserResponseModel updateResponse = given(updateRequestSpec)
                    .header("Authorization", "Bearer " + actualAccessToken)
                    .body(updateData)
                    .when()
                    .patch("/users/me/")
                    .then()
                    .spec(successfulUpdateResponseSpec)
                    .extract()
                    .as(SuccessfulUpdateUserResponseModel.class);

            assertThat(updateResponse.id()).isNotNull();
            assertThat(updateResponse.username()).isEqualTo(username);
            assertThat(updateResponse.firstName()).isEqualTo(firstName);
            assertThat(updateResponse.lastName()).isEqualTo(lastName);
            assertThat(updateResponse.email()).isEqualTo("");

        });

        step("Проверка изменений методом get и проверка ответа (200)", () -> {
            SuccessfulUpdateUserResponseModel updatedUserData = given()
                    .filter(withCustomTemplate())
                    .header("Authorization", "Bearer " + actualAccessToken)
                    .when()
                    .get("/users/me/")
                    .then()
                    .spec(updatedUserDataResponseSpec)
                    .extract()
                    .as(SuccessfulUpdateUserResponseModel.class);

            assertThat(updatedUserData.id()).isNotNull();
            assertThat(updatedUserData.username()).isEqualTo(username);
            assertThat(updatedUserData.firstName()).isEqualTo(firstName);
            assertThat(updatedUserData.lastName()).isEqualTo(lastName);
            assertThat(updatedUserData.email()).isEqualTo("");
        });
    }

    @Test
    @DisplayName("Тест на проверку изменения выборочных данных пользователя методом PUT")
    public void partialUpdateUserWithPutMethodNegativeTest() {

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
        String actualAccessToken = step("Авторизация и получение access-токена", () ->
                given(loginRequestSpec)
                        .body(loginData)
                        .when()
                        .post("/auth/token/")
                        .then()
                        .spec(successfulLoginResponseSpec)
                        .extract()
                        .path("access"));

        PartialUpdateBodyModel updateData = new PartialUpdateBodyModel(firstName, lastName);
        PartialWithPutMethodUpdateUserResponseModel updateResponse =  step("Отправка запроса put с access-токеном и проверка ответа (400)", () -> {
           return given(updateRequestSpec)
                    .header("Authorization", "Bearer " + actualAccessToken)
                    .body(updateData)
                    .when()
                    .put("/users/me/")
                    .then()
                    .spec(partialWithPutMethodUpdateResponseSpec)
                    .extract()
                    .as(PartialWithPutMethodUpdateUserResponseModel.class);

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
