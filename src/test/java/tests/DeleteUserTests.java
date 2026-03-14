package tests;

import models.login.LoginBodyModel;
import models.login.SuccessfulLoginResponseModel;
import models.registration.RegistrationBodyModel;
import models.registration.SuccessfulRegistrationResponseModel;
import models.update.*;
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
import static specs.login.LoginSpec.loginRequestSpec;
import static specs.login.LoginSpec.successfulLoginResponseSpec;
import static specs.registration.RegistrationSpec.registrationRequestSpec;
import static specs.registration.RegistrationSpec.successfulRegistrationResponseSpec;
import static specs.update.UpdateSpec.*;

public class DeleteUserTests extends TestBase {

    String username;
    String password;
    String firstName;
    String lastName;
    String email;

    @BeforeEach
    public void prepareTestData() {
        Faker faker = new Faker();
        username = faker.name().firstName();
        password = faker.text().text(Text.TextSymbolsBuilder.builder().len(8).with(EN_UPPERCASE, 2).with(DIGITS, 3).build());
        firstName = faker.name().firstName();
        lastName = faker.name().lastName();
        email = faker.internet().emailAddress();
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
            assertThat(registrationResponse.firstName()).isEqualTo("");
            assertThat(registrationResponse.lastName()).isEqualTo("");
            assertThat(registrationResponse.email()).isEqualTo("");

            String ipAddrRegexp = "^((25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)\\.){3}"
                    + "(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)$";
            assertThat(registrationResponse.remoteAddr()).matches(ipAddrRegexp);
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
                    assertThat(registrationResponse.firstName()).isEqualTo("");
                    assertThat(registrationResponse.lastName()).isEqualTo("");
                    assertThat(registrationResponse.email()).isEqualTo("");

                    String ipAddrRegexp = "^((25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)\\.){3}"
                            + "(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)$";
                    assertThat(registrationResponse.remoteAddr()).matches(ipAddrRegexp);
                });

        step("Удаление пользователя без авторизации и проверка ответа (401)", () -> {
            NotProvidedAuthenticationCredentialsResponseModel deleteResponse = given()
                    .filter(withCustomTemplate())
                    .log().all()
                    .when()
                    .delete("/users/me/")
                    .then()
                    .spec(notProvidedAuthenticationCredentialsResponseSpec)
                    .extract()
                    .as(NotProvidedAuthenticationCredentialsResponseModel.class);

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
