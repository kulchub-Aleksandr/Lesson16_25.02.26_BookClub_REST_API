package tests;

import models.bookClubRegistration.BookClubRegistrationBodyModel;
import models.bookClubRegistration.SuccessfulBookClubRegistrationResponseModel;
import models.login.LoginBodyModel;
import models.login.SuccessfulLoginResponseModel;
import models.registration.RegistrationBodyModel;
import models.registration.SuccessfulRegistrationResponseModel;
import net.datafaker.Faker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static specs.bookClubRegistration.BookClubRegistrationSpec.bookClubRegistrationRequestSpec;
import static specs.bookClubRegistration.BookClubRegistrationSpec.bookClubSuccessfulRegistrationResponseSpec;
import static specs.login.LoginSpec.loginRequestSpec;
import static specs.login.LoginSpec.successfulLoginResponseSpec;
import static specs.registration.RegistrationSpec.registrationRequestSpec;
import static specs.registration.RegistrationSpec.successfulRegistrationResponseSpec;

public class BookClubRegistrationTests extends TestBase {

    String username;
    String password;
    String bookTitle;
    String bookAuthors;
    Integer publicationYear;
    String description;
    String telegramChatLink;


    @BeforeEach
    public void prepareTestData() {
        Faker faker = new Faker();
        username = faker.name().firstName();
        password = faker.name().firstName();
        bookTitle = faker.book().title();
        bookAuthors = faker.book().author();
        publicationYear = faker.number().numberBetween(1800, java.time.Year.now().getValue());
        description = faker.book().title();
        telegramChatLink = faker.internet().url();


    }

    @Test
    public void successfulBookClubRegistrationTest() {

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

        LoginBodyModel loginData = new LoginBodyModel(username, password);

        SuccessfulLoginResponseModel loginResponse = given(loginRequestSpec)
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



        BookClubRegistrationBodyModel registrationClubData = new BookClubRegistrationBodyModel(
                bookTitle,
                bookAuthors,
                publicationYear,
                description,
                telegramChatLink);


        SuccessfulBookClubRegistrationResponseModel registrationClubResponse = given(bookClubRegistrationRequestSpec)
                .header("Authorization", "Bearer " + actualAccess)
                .body(registrationClubData)
                .when()
                .post("/clubs/")
                .then()
                .spec(bookClubSuccessfulRegistrationResponseSpec)
                .extract()
                .as(SuccessfulBookClubRegistrationResponseModel.class);

        String actualBookTitle = registrationClubResponse.bookTitle();
        assertThat(actualBookTitle).isEqualTo(bookTitle);
    }
}
