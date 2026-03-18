package tests;

import models.login.*;
import models.registration.RegistrationBodyModel;
import models.registration.SuccessfulRegistrationResponseModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static allure.CustomAllureListener.withCustomTemplate;
import static io.qameta.allure.Allure.step;
import static io.restassured.RestAssured.given;
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

        SuccessfulRegistrationResponseModel registrationResponse =
                step("Регистрация нового пользователя", () -> {
                    RegistrationBodyModel registrationData = new RegistrationBodyModel(username, password);
                    return given(registrationRequestSpec)
                            .body(registrationData)
                            .when()
                            .post("/users/register/")
                            .then()
                            .spec(successfulRegistrationResponseSpec)
                            .extract()
                            .as(SuccessfulRegistrationResponseModel.class);
                });
        step("Проверка корректности зарегистрированных данных", () -> {
            assertThat(registrationResponse.id()).isGreaterThan(0);
            assertThat(registrationResponse.username()).isEqualTo(username);
        });

        SuccessfulLoginResponseModel loginResponse =
                step("Авторизация существующего пользователя и проверка ответа ", () -> {
                    LoginBodyModel loginData = new LoginBodyModel(username, password);
                    return given(registrationRequestSpec)
                            .body(loginData)
                            .when()
                            .post("/auth/token/")
                            .then()
                            .spec(successfulLoginResponseSpec)
                            .extract()
                            .as(SuccessfulLoginResponseModel.class);
                });
        step("Проверка корректности полученных токенов", () -> {
            String expectedTokenPath = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9";
            String actualAccess = loginResponse.access();
            String actualRefresh = loginResponse.refresh();

            assertThat(actualAccess).startsWith(expectedTokenPath);
            assertThat(actualRefresh).startsWith(expectedTokenPath);
            assertThat(actualAccess).isNotEqualTo(actualRefresh);

        });
        String accessToken =
                step("Авторизация и получение токена для удаления пользователя ", () -> {
                    LoginBodyModel loginData = new LoginBodyModel(username, password);
                    return given(loginRequestSpec)
                            .body(loginData)
                            .when()
                            .post("/auth/token/")
                            .then()
                            .spec(successfulLoginResponseSpec)
                            .extract()
                            .path("access");
                });

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

        SuccessfulRegistrationResponseModel registrationResponse =
                step("Регистрация нового пользователя", () -> {
                    RegistrationBodyModel registrationData = new RegistrationBodyModel(username, password);
                    return given(registrationRequestSpec)
                            .body(registrationData)
                            .when()
                            .post("/users/register/")
                            .then()
                            .spec(successfulRegistrationResponseSpec)
                            .extract()
                            .as(SuccessfulRegistrationResponseModel.class);
                });
        step("Проверка корректности зарегистрированных данных", () -> {
            assertThat(registrationResponse.id()).isGreaterThan(0);
            assertThat(registrationResponse.username()).isEqualTo(username);
        });


        WrongCredentialsLoginResponseModel loginResponse =
                step("Авторизация с применением не правильного пароля и проверка ответа (401)", () -> {
                    LoginBodyModel wrongLoginData = new LoginBodyModel(username, wrongPassword);

                    return given(loginRequestSpec)
                            .body(wrongLoginData)
                            .when()
                            .post("/auth/token/")
                            .then()
                            .spec(wrongCredentialsLoginResponseSpec)
                            .extract()
                            .as(WrongCredentialsLoginResponseModel.class);
                });
        step("Проверка текста ошибки в ответе", () -> {
            String expectedDetailError = "Invalid username or password.";
            String actualDetailError = loginResponse.detail();

            assertThat(actualDetailError).isEqualTo(expectedDetailError);
        });

        String actualAccessToken = step("Авторизация и получение access-токена для удаления пользователя", () -> {
            LoginBodyModel loginData = new LoginBodyModel(username, password);
            return given(loginRequestSpec)
                    .body(loginData)
                    .when()
                    .post("/auth/token/")
                    .then()
                    .spec(successfulLoginResponseSpec)
                    .extract()
                    .path("access");
        });

        step("Удаление пользователя", () -> {
            given()
                    .filter(withCustomTemplate())
                    .log().all()
                    .header("Authorization", "Bearer " + actualAccessToken)
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
        SuccessfulRegistrationResponseModel registrationResponse =
                step("Регистрация нового пользователя", () -> {
                    RegistrationBodyModel registrationData = new RegistrationBodyModel(username, password);
                    return given(registrationRequestSpec)
                            .body(registrationData)
                            .when()
                            .post("/users/register/")
                            .then()
                            .spec(successfulRegistrationResponseSpec)
                            .extract()
                            .as(SuccessfulRegistrationResponseModel.class);
                });
        step("Проверка корректности зарегистрированных данных", () -> {
            assertThat(registrationResponse.id()).isGreaterThan(0);
            assertThat(registrationResponse.username()).isEqualTo(username);
        });
        EmptyPasswordResponseModel loginResponse =
                step("Авторизация с применением пустого поля для пароля и проверка ответа (400)", () -> {
                    LoginBodyModel wrongLoginData = new LoginBodyModel(username, emptyPassword);

                    return given(loginRequestSpec)
                            .body(wrongLoginData)
                            .when()
                            .post("/auth/token/")
                            .then()
                            .spec(emptyPasswordLoginResponseSpec)
                            .extract()
                            .as(EmptyPasswordResponseModel.class);
                });
        step("Проверка текста ошибки в ответе", () -> {
            String expectedDetailError = "This field may not be blank.";
            String actualDetailError = loginResponse.password().getFirst();

            assertThat(actualDetailError).isEqualTo(expectedDetailError);
        });

        String actualAccessToken = step("Авторизация и получение access-токена для удаления пользователя", () -> {
            LoginBodyModel loginData = new LoginBodyModel(username, password);
            return given(loginRequestSpec)
                    .body(loginData)
                    .when()
                    .post("/auth/token/")
                    .then()
                    .spec(successfulLoginResponseSpec)
                    .extract()
                    .path("access");
        });

        step("Удаление пользователя", () -> {
            given()
                    .filter(withCustomTemplate())
                    .log().all()
                    .header("Authorization", "Bearer " + actualAccessToken)
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
        SuccessfulRegistrationResponseModel registrationResponse =
                step("Регистрация нового пользователя", () -> {
                    RegistrationBodyModel registrationData = new RegistrationBodyModel(username, password);
                    return given(registrationRequestSpec)
                            .body(registrationData)
                            .when()
                            .post("/users/register/")
                            .then()
                            .spec(successfulRegistrationResponseSpec)
                            .extract()
                            .as(SuccessfulRegistrationResponseModel.class);
                });
        step("Проверка корректности зарегистрированных данных", () -> {
            assertThat(registrationResponse.id()).isGreaterThan(0);
            assertThat(registrationResponse.username()).isEqualTo(username);
        });


        WrongCredentialsLoginResponseModel loginResponse =
                step("Авторизация с применением не правильного логина и проверка ответа (401)", () -> {
                    LoginBodyModel wrongLoginData = new LoginBodyModel(wrongUsername, password);
                    return given(loginRequestSpec)
                            .body(wrongLoginData)
                            .when()
                            .post("/auth/token/")
                            .then()
                            .spec(wrongCredentialsLoginResponseSpec)
                            .extract()
                            .as(WrongCredentialsLoginResponseModel.class);
                });
        step("Проверка текста ошибки в ответе", () -> {
            String expectedDetailError = "Invalid username or password.";
            String actualDetailError = loginResponse.detail();

            assertThat(actualDetailError).isEqualTo(expectedDetailError);
        });

        String actualAccessToken = step("Авторизация и получение access-токена для удаления пользователя", () -> {
            LoginBodyModel loginData = new LoginBodyModel(username, password);
            return given(loginRequestSpec)
                    .body(loginData)
                    .when()
                    .post("/auth/token/")
                    .then()
                    .spec(successfulLoginResponseSpec)
                    .extract()
                    .path("access");
        });

        step("Удаление пользователя", () -> {
            given()
                    .filter(withCustomTemplate())
                    .log().all()
                    .header("Authorization", "Bearer " + actualAccessToken)
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
        SuccessfulRegistrationResponseModel registrationResponse =
                step("Регистрация нового пользователя", () -> {
                    RegistrationBodyModel registrationData = new RegistrationBodyModel(username, password);
                    return given(registrationRequestSpec)
                            .body(registrationData)
                            .when()
                            .post("/users/register/")
                            .then()
                            .spec(successfulRegistrationResponseSpec)
                            .extract()
                            .as(SuccessfulRegistrationResponseModel.class);
                });
        step("Проверка корректности зарегистрированных данных", () -> {
            assertThat(registrationResponse.id()).isGreaterThan(0);
            assertThat(registrationResponse.username()).isEqualTo(username);
        });

        EmptyUserResponseModel loginResponse =
                step("Авторизация с применением пустого поля для логина и проверка ответа (400)", () -> {
                    LoginBodyModel wrongLoginData = new LoginBodyModel(emptyUsername, password);
                    return given(loginRequestSpec)
                            .body(wrongLoginData)
                            .when()
                            .post("/auth/token/")
                            .then()
                            .spec(emptyUserLoginResponseSpec)
                            .extract()
                            .as(EmptyUserResponseModel.class);
                });
        step("Проверка текста ошибки в ответе", () -> {
            String expectedDetailError = "This field may not be blank.";
            String actualDetailError = loginResponse.username().getFirst();

            assertThat(actualDetailError).isEqualTo(expectedDetailError);
        });

        String actualAccessToken = step("Авторизация и получение access-токена для удаления пользователя", () -> {
            LoginBodyModel loginData = new LoginBodyModel(username, password);
            return given(loginRequestSpec)
                    .body(loginData)
                    .when()
                    .post("/auth/token/")
                    .then()
                    .spec(successfulLoginResponseSpec)
                    .extract()
                    .path("access");
        });

        step("Удаление пользователя", () -> {
            given()
                    .filter(withCustomTemplate())
                    .log().all()
                    .header("Authorization", "Bearer " + actualAccessToken)
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
        SuccessfulRegistrationResponseModel registrationResponse =
                step("Регистрация нового пользователя", () -> {
                    RegistrationBodyModel registrationData = new RegistrationBodyModel(username, password);
                    return given(registrationRequestSpec)
                            .body(registrationData)
                            .when()
                            .post("/users/register/")
                            .then()
                            .spec(successfulRegistrationResponseSpec)
                            .extract()
                            .as(SuccessfulRegistrationResponseModel.class);
                });
        step("Проверка корректности зарегистрированных данных", () -> {
            assertThat(registrationResponse.id()).isGreaterThan(0);
            assertThat(registrationResponse.username()).isEqualTo(username);
        });

        EmptyUserEmptyPasswordResponseModel loginResponse =
                step("Авторизация с применением пустого поля для логина и пароля с проверкой ответа (400)", () -> {
                    LoginBodyModel wrongLoginData = new LoginBodyModel(emptyUsername, emptyPassword);
                    return given(loginRequestSpec)
                            .body(wrongLoginData)
                            .when()
                            .post("/auth/token/")
                            .then()
                            .spec(emptyUserEmptyPasswordLoginResponseSpec)
                            .extract()
                            .as(EmptyUserEmptyPasswordResponseModel.class);
                });
        step("Проверка текста ошибки в ответе", () -> {
            String expectedDetailError = "This field may not be blank.";
            String actualDetailError_1 = loginResponse.username().getFirst();
            String actualDetailError_2 = loginResponse.password().getFirst();

            assertThat(actualDetailError_1).isEqualTo(expectedDetailError);
            assertThat(actualDetailError_2).isEqualTo(expectedDetailError);
        });

        String actualAccessToken = step("Авторизация и получение access-токена для удаления пользователя", () -> {
            LoginBodyModel loginData = new LoginBodyModel(username, password);
            return given(loginRequestSpec)
                    .body(loginData)
                    .when()
                    .post("/auth/token/")
                    .then()
                    .spec(successfulLoginResponseSpec)
                    .extract()
                    .path("access");
        });

        step("Удаление пользователя", () -> {
            given()
                    .filter(withCustomTemplate())
                    .log().all()
                    .header("Authorization", "Bearer " + actualAccessToken)
                    .when()
                    .delete("/users/me/")
                    .then()
                    .log().all()
                    .statusCode(204);
        });
    }

}
