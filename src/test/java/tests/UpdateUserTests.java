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

public class UpdateUserTests extends TestBase {

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
            assertThat(registrationResponse.firstName()).isEqualTo("");
            assertThat(registrationResponse.lastName()).isEqualTo("");
            assertThat(registrationResponse.email()).isEqualTo("");

            String ipAddrRegexp = "^((25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)\\.){3}"
                    + "(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)$";
            assertThat(registrationResponse.remoteAddr()).matches(ipAddrRegexp);

            //String registrationIpAddress = registrationResponse.remoteAddr();
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

//        String actualAccessToken = loginResponse.access();
//        String actualRefreshToken = loginResponse.refresh();

        //assertThat(actualAccessToken).isNotEqualTo(actualRefreshToken);

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

            // assertThat(updateResponse.id()).isEqualTo(registrationResponse.id());
            assertThat(updateResponse.username()).isEqualTo(username);
            assertThat(updateResponse.firstName()).isEqualTo(firstName);
            assertThat(updateResponse.lastName()).isEqualTo(lastName);
            assertThat(updateResponse.email()).isEqualTo(email);
            //assertThat(registrationResponse.remoteAddr()).matches(ipAddrRegexp);

            //String updateIpAddress = updateResponse.remoteAddr();
            //assertThat(registrationIpAddress).isEqualTo(updateIpAddress);
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

            //assertThat(updatedUserData.id()).isEqualTo(registrationResponse.id());
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
            NotProvidedAuthenticationCredentialsResponseModel updateResponse = given(updateRequestSpec)
                    .body(updateData)
                    .when()
                    .put("/users/me/")
                    .then()
                    .spec(notProvidedAuthenticationCredentialsResponseSpec)
                    .extract()
                    .as(NotProvidedAuthenticationCredentialsResponseModel.class);

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
            assertThat(registrationResponse.firstName()).isEqualTo("");
            assertThat(registrationResponse.lastName()).isEqualTo("");
            assertThat(registrationResponse.email()).isEqualTo("");

            String ipAddrRegexp = "^((25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)\\.){3}"
                    + "(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)$";
            assertThat(registrationResponse.remoteAddr()).matches(ipAddrRegexp);

            //String registrationIpAddress = registrationResponse.remoteAddr();
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

                    //assertThat(updateResponse.id()).isEqualTo(registrationResponse.id());
                    assertThat(updateResponse.username()).isEqualTo(username);
                    assertThat(updateResponse.firstName()).isEqualTo(firstName);
                    assertThat(updateResponse.lastName()).isEqualTo(lastName);
                    assertThat(updateResponse.email()).isEqualTo("");
                    // assertThat(registrationResponse.remoteAddr()).matches(ipAddrRegexp);

                    String updateIpAddress = updateResponse.remoteAddr();
                    //assertThat(registrationIpAddress).isEqualTo(updateIpAddress);
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

            //assertThat(updatedUserData.id()).isEqualTo(registrationResponse.id());
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
            assertThat(registrationResponse.firstName()).isEqualTo("");
            assertThat(registrationResponse.lastName()).isEqualTo("");
            assertThat(registrationResponse.email()).isEqualTo("");

            String ipAddrRegexp = "^((25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)\\.){3}"
                    + "(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)$";
            assertThat(registrationResponse.remoteAddr()).matches(ipAddrRegexp);

            //String registrationIpAddress = registrationResponse.remoteAddr();
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
        step("Отправка запроса put с access-токеном и проверка ответа (400)", () -> {
            PartialWithPutMethodUpdateUserResponseModel updateResponse = given(updateRequestSpec)
                    .header("Authorization", "Bearer " + actualAccessToken)
                    .body(updateData)
                    .when()
                    .put("/users/me/")
                    .then()
                    .spec(partialWithPutMethodUpdateResponseSpec)
                    .extract()
                    .as(PartialWithPutMethodUpdateUserResponseModel.class);

            String actualUsername = updateResponse.username().getFirst();
            String expectedUsername = "This field is required.";
            String actualEmail = updateResponse.email().getFirst();
            String expectedEmail = "This field is required.";

            assertThat(actualUsername).isEqualTo(expectedUsername);
            assertThat(actualEmail).isEqualTo(expectedEmail);
        });
    }

}
