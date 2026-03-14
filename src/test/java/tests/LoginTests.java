package tests;

import io.qameta.allure.restassured.AllureRestAssured;
import models.login.*;
import models.registration.RegistrationBodyModel;
import models.registration.SuccessfulRegistrationResponseModel;
import net.datafaker.Faker;
import net.datafaker.providers.base.Text;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static allure.CustomAllureListener.withCustomTemplate;
import static io.qameta.allure.Allure.step;
import static io.restassured.RestAssured.given;
import static net.datafaker.providers.base.Text.DIGITS;
import static net.datafaker.providers.base.Text.EN_UPPERCASE;
import static org.assertj.core.api.Assertions.assertThat;
import static specs.login.LoginSpec.*;
import static specs.registration.RegistrationSpec.registrationRequestSpec;
import static specs.registration.RegistrationSpec.successfulRegistrationResponseSpec;

public class LoginTests extends TestBase {
    private final TestData testData = new TestData();
    private String username;
    private String password;
    private String wrongPassword;
    private String wrongUsername;
    private String emptyPassword;
    private String emptyUsername;

    @BeforeEach
    public void prepareTestData() {
        username = testData.getUsername();
        password = testData.getPassword();
        wrongPassword = password + "1234";
        wrongUsername = username + "qa";
        emptyPassword = testData.getEmptyPassword();
        emptyUsername = testData.getEmptyUsername();

    }

    @Test
    @DisplayName("Тест на проверку авторизации существующего пользователя")
    public void successfulLoginTest() {

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

        step("Авторизация существующего пользователя и проверка ответа (200)", () -> {
            SuccessfulLoginResponseModel loginResponse = given(registrationRequestSpec)
                    .body(loginData)
                    .when()
                    .post("/auth/token/")
                    .then()
                    .spec(successfulLoginResponseSpec)
                    .extract()
                    .as(SuccessfulLoginResponseModel.class);

            String expectedTokenPath = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9";

            String actualAccess = loginResponse.access();
            String actualRefresh = loginResponse.refresh();

            assertThat(actualAccess).startsWith(expectedTokenPath);
            assertThat(actualRefresh).startsWith(expectedTokenPath);
            assertThat(actualAccess).isNotEqualTo(actualRefresh);
        });

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

    @Test
    @DisplayName("Тест на проверку авторизации пользователя с применением не правильного пароля")
    public void wrongPasswordLoginNegativeTest() {

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

        LoginBodyModel wrongLoginData = new LoginBodyModel(username, wrongPassword);

        step("Авторизация с применением не правильного пароля и проверка ответа (401)", () -> {
            WrongCredentialsLoginResponseModel loginResponse = given(loginRequestSpec)
                    .body(wrongLoginData)
                    .when()
                    .post("/auth/token/")
                    .then()
                    .spec(wrongCredentialsLoginResponseSpec)
                    .extract()
                    .as(WrongCredentialsLoginResponseModel.class);

            String expectedDetailError = "Invalid username or password.";
            String actualDetailError = loginResponse.detail();

            assertThat(actualDetailError).isEqualTo(expectedDetailError);
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

    @Test
    @DisplayName("Тест на проверку авторизации пользователя с применением пустого поля для пароля")
    public void emptyPasswordLoginNegativeTest() {
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

        LoginBodyModel wrongLoginData = new LoginBodyModel(username, emptyPassword);
        step("Авторизация с применением пустого поля для пароля и проверка ответа (400)", () -> {
            EmptyPasswordResponseModel loginResponse = given(loginRequestSpec)
                    .body(wrongLoginData)
                    .when()
                    .post("/auth/token/")
                    .then()
                    .spec(emptyPasswordLoginResponseSpec)
                    .extract()
                    .as(EmptyPasswordResponseModel.class);

            String expectedDetailError = "This field may not be blank.";
            String actualDetailError = loginResponse.password().getFirst();

            assertThat(actualDetailError).isEqualTo(expectedDetailError);
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

    @Test
    @DisplayName("Тест на проверку авторизации пользователя с применением не правильного логина")
    public void wrongUserNameLoginNegativeTest() {

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

        LoginBodyModel wrongLoginData = new LoginBodyModel(wrongUsername, password);

        step("Авторизация с применением не правильного логина и проверка ответа (401)", () -> {
            WrongCredentialsLoginResponseModel loginResponse = given(loginRequestSpec)
                    .body(wrongLoginData)
                    .when()
                    .post("/auth/token/")
                    .then()
                    .spec(wrongCredentialsLoginResponseSpec)
                    .extract()
                    .as(WrongCredentialsLoginResponseModel.class);

            String expectedDetailError = "Invalid username or password.";
            String actualDetailError = loginResponse.detail();

            assertThat(actualDetailError).isEqualTo(expectedDetailError);
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

    @Test
    @DisplayName("Тест на проверку авторизации пользователя с применением пустого поля для логина")
    public void emptyUserNameLoginNegativeTest() {
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

        LoginBodyModel wrongLoginData = new LoginBodyModel(emptyUsername, password);

        step("Авторизация с применением пустого поля для логина и проверка ответа (400)", () -> {
            EmptyUserResponseModel loginResponse = given(loginRequestSpec)
                    .body(wrongLoginData)
                    .when()
                    .post("/auth/token/")
                    .then()
                    .spec(emptyUserLoginResponseSpec)
                    .extract()
                    .as(EmptyUserResponseModel.class);

            String expectedDetailError = "This field may not be blank.";
            String actualDetailError = loginResponse.username().getFirst();

            assertThat(actualDetailError).isEqualTo(expectedDetailError);
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

    @Test
    @DisplayName("Тест на проверку авторизации пользователя с применением пустого поля для логина и пустого поля для пароля")
    public void emptyUserNameEmptyPasswordLoginNegativeTest() {
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

        LoginBodyModel wrongLoginData = new LoginBodyModel(emptyUsername, emptyPassword);
        step("Авторизация с применением пустого поля для логина и пароля с проверкой ответа (400)", () -> {
            EmptyUserEmptyPasswordResponseModel loginResponse = given(loginRequestSpec)
                    .body(wrongLoginData)
                    .when()
                    .post("/auth/token/")
                    .then()
                    .spec(emptyUserEmptyPasswordLoginResponseSpec)
                    .extract()
                    .as(EmptyUserEmptyPasswordResponseModel.class);

            String expectedDetailError = "This field may not be blank.";
            String actualDetailError_1 = loginResponse.username().getFirst();
            String actualDetailError_2 = loginResponse.password().getFirst();

            assertThat(actualDetailError_1).isEqualTo(expectedDetailError);
            assertThat(actualDetailError_2).isEqualTo(expectedDetailError);
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
