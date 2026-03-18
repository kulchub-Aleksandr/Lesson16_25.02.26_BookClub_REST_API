package tests;

import models.login.LoginBodyModel;
import models.registration.*;
import net.datafaker.Faker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static allure.CustomAllureListener.withCustomTemplate;
import static io.qameta.allure.Allure.step;
import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static specs.login.LoginSpec.loginRequestSpec;
import static specs.login.LoginSpec.successfulLoginResponseSpec;
import static specs.registration.RegistrationSpec.*;

public class RegistrationTests extends TestBase {
    private final TestData testData = new TestData();
    private String username;
    private String password;

    @BeforeEach
    public void prepareTestData() {
        username = testData.getUsername();
        password = testData.getPassword();
    }

    @Test
    @DisplayName("Тест на проверку регистрации нового пользователя")
    public void successfulRegistrationTest() {

        SuccessfulRegistrationResponseModel registrationResponse =
                step("Регистрация нового пользователя и проверка ответа (201)", () -> {
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
            assertThat(registrationResponse.firstName()).isEqualTo("");
            assertThat(registrationResponse.lastName()).isEqualTo("");
            assertThat(registrationResponse.email()).isEqualTo("");

            String ipAddrRegexp = "^((25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)\\.){3}"
                    + "(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)$";
            assertThat(registrationResponse.remoteAddr()).matches(ipAddrRegexp);
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
    @DisplayName("Тест на проверку регистрации пользователя с уже существующими регистрационными данными")
    public void existingUserRegistrationNegativeTest() {

        SuccessfulRegistrationResponseModel registrationResponse_1 =
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

        step("Проверка соответствия отправленных данных с данными в ответе", () -> {
            assertThat(registrationResponse_1.username()).isEqualTo(username);
        });

        ExistingUserResponseModel registrationResponse_2 =
                step("Регистрация нового пользователя с уже существующими регистрационными данными и проверка ответа (400)", () -> {
                    RegistrationBodyModel registrationData = new RegistrationBodyModel(username, password);
                    return given(registrationRequestSpec)
                            .body(registrationData)
                            .when()
                            .post("/users/register/")
                            .then()
                            .spec(existingUserRegistrationResponseSpec)
                            .extract()
                            .as(ExistingUserResponseModel.class);
                });
        step("Проверка текста ошибки в ответе", () -> {
            String expectedError = "A user with that username already exists.";
            String actualError = registrationResponse_2.username().getFirst();
            assertThat(actualError).isEqualTo(expectedError);
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
    @DisplayName("Тест на проверку регистрации пользователя с невалидными регистрационными данными")
    public void invalidUserNameRegistrationNegativeTest() {

        Faker faker = new Faker();
        String username = faker.name().fullName();
        String password = faker.name().firstName();

        RegistrationBodyModel registrationData = new RegistrationBodyModel(username, password);
        InvalidUserNameResponseModel registrationResponse =
                step("Регистрация нового пользователя с невалидными регистрационными данными и проверка ответа (400)", () -> {
                    return given(registrationRequestSpec)
                            .body(registrationData)
                            .when()
                            .post("/users/register/")
                            .then()
                            .spec(invalidUserNameRegistrationResponseSpec)
                            .extract()
                            .as(InvalidUserNameResponseModel.class);
                });
        step("Проверка текста ошибки в ответе", () -> {
            String expectedError = "Enter a valid username. This value may contain only letters, numbers, and @/./+/-/_ characters.";
            String actualError = registrationResponse.username().getFirst();
            assertThat(actualError).isEqualTo(expectedError);
        });

    }

    @Test
    @DisplayName("Тест на проверку регистрации пользователя с неподдерживаемым типом передаваемых данных")
    public void unsupportedMediaTypeRegistrationNegativeTest() {

        RegistrationBodyModel registrationData = new RegistrationBodyModel(username, password);
        UnsupportedMediaTypeResponseModel registrationResponse =
                step("Регистрация нового пользователя с неподдерживаемым типом передаваемых данных и проверка ответа (415)", () -> {
                    return given()
                            .filter(withCustomTemplate())
                            .log().all()
                            .body(registrationData)
                            .when()
                            .post("/users/register/")
                            .then()
                            .spec(unsupportedMediaTypeResponseSpec)
                            .extract()
                            .as(UnsupportedMediaTypeResponseModel.class);
                });
        step("Проверка текста ошибки в ответе", () -> {
            String expectedError = "Unsupported media type \"text/plain; charset=ISO-8859-1\" in request.";
            String actualError = registrationResponse.detail();
            assertThat(actualError).isEqualTo(expectedError);
        });
    }
}
