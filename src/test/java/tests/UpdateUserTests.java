package tests;

import models.login.LoginBodyModel;
import models.login.SuccessfulLoginResponseModel;
import models.registration.RegistrationBodyModel;
import models.registration.SuccessfulRegistrationResponseModel;
import models.update.SuccessfulUpdateUserResponseModel;
import models.update.UpdateBodyModel;
import net.datafaker.Faker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static specs.login.LoginSpec.loginRequestSpec;
import static specs.login.LoginSpec.successfulLoginResponseSpec;
import static specs.registration.RegistrationSpec.registrationRequestSpec;
import static specs.registration.RegistrationSpec.successfulRegistrationResponseSpec;
import static specs.update.UpdateSpec.successfulUpdateResponseSpec;
import static specs.update.UpdateSpec.updateRequestSpec;

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
        password = faker.name().firstName();
        firstName = faker.name().firstName();
        lastName = faker.name().lastName();
        email = faker.internet().emailAddress();
    }


    @Test
    public void successfulUpdateUserTest() {

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

        String registrationIpAddress = registrationResponse.remoteAddr();

        LoginBodyModel loginData = new LoginBodyModel(username, password);
        SuccessfulLoginResponseModel loginResponse = given(loginRequestSpec)
                .body(loginData)
                .when()
                .post("/auth/token/")
                .then()
                .spec(successfulLoginResponseSpec)
                .extract()
                .as(SuccessfulLoginResponseModel.class);


        String actualAccessToken = loginResponse.access();
        String actualRefreshToken = loginResponse.refresh();

        assertThat(actualAccessToken).isNotEqualTo(actualRefreshToken);

        UpdateBodyModel updateData = new UpdateBodyModel(username,
                firstName, lastName, email);

        SuccessfulUpdateUserResponseModel updateResponse = given(updateRequestSpec)
                .header("Authorization", "Bearer " + actualAccessToken)
                .body(updateData)
                .when()
                .patch("/users/me/")
                .then()
                .spec(successfulUpdateResponseSpec)
                .extract()
                .as(SuccessfulUpdateUserResponseModel.class);

        assertThat(updateResponse.id()).isGreaterThan(0);
        assertThat(updateResponse.username()).isEqualTo(username);
        assertThat(updateResponse.firstName()).isEqualTo(firstName);
        assertThat(updateResponse.lastName()).isEqualTo(lastName);
        assertThat(updateResponse.email()).isEqualTo(email);
        assertThat(registrationResponse.remoteAddr()).matches(ipAddrRegexp);
        String updateIpAddress = updateResponse.remoteAddr();
        assertThat(registrationIpAddress).isEqualTo(updateIpAddress);


    }


    @Test
    public void UpdateUser401Test() {

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

        UpdateBodyModel updateData = new UpdateBodyModel(username,
                firstName, lastName, email);

        given(updateRequestSpec)
                .body(updateData)
                .when()
                .put("/users/me/")
                .then()
                .spec(successfulUpdateResponseSpec)
                .extract()
                .as(UpdateBodyModel.class);

    }


    // todo add update user tests
}
