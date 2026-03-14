package tests;

import models.bookClubRegistration.BookClubRegistrationBodyModel;
import models.bookClubRegistration.SuccessfulBookClubRegistrationResponseModel;
import models.login.LoginBodyModel;
import models.login.SuccessfulLoginResponseModel;
import models.registration.RegistrationBodyModel;
import models.registration.SuccessfulRegistrationResponseModel;
import net.datafaker.Faker;
import net.datafaker.providers.base.Text;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.qameta.allure.Allure.step;
import static io.restassured.RestAssured.given;
import static net.datafaker.providers.base.Text.DIGITS;
import static net.datafaker.providers.base.Text.EN_UPPERCASE;
import static org.assertj.core.api.Assertions.assertThat;
import static specs.bookClubRegistration.BookClubRegistrationSpec.bookClubRegistrationRequestSpec;
import static specs.bookClubRegistration.BookClubRegistrationSpec.bookClubSuccessfulRegistrationResponseSpec;
import static specs.login.LoginSpec.loginRequestSpec;
import static specs.login.LoginSpec.successfulLoginResponseSpec;
import static specs.registration.RegistrationSpec.registrationRequestSpec;
import static specs.registration.RegistrationSpec.successfulRegistrationResponseSpec;

public class BookClubRegistrationTests extends TestBase {
    private final TestData testData = new TestData();
    private String username;
    private String password;

    String bookTitle;
    String bookAuthors;
    Integer publicationYear;
    String description;
    String telegramChatLink;


    @BeforeEach
    public void prepareTestData() {
        username = testData.getUsername();
        password = testData.getPassword();
        bookTitle = testData.getBookTitle();
        bookAuthors = testData.getBookAuthor();
        publicationYear = testData.getPublicationYear();
        description = testData.getBookDescription();
        telegramChatLink = testData.getTelegramChatLink();


    }

    @Test
    @DisplayName("Тест на проверку регистрации нового клуба")
    public void successfulBookClubRegistrationTest() {
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



        BookClubRegistrationBodyModel registrationClubData = new BookClubRegistrationBodyModel(
                bookTitle,
                bookAuthors,
                publicationYear,
                description,
                telegramChatLink);

        step("Регистрация нового клуба  и проверка ответа (201)", () -> {
            SuccessfulBookClubRegistrationResponseModel registrationClubResponse = given(bookClubRegistrationRequestSpec)
                    .header("Authorization", "Bearer " + actualAccessToken)
                    .body(registrationClubData)
                    .when()
                    .post("/clubs/")
                    .then()
                    .spec(bookClubSuccessfulRegistrationResponseSpec)
                    .extract()
                    .as(SuccessfulBookClubRegistrationResponseModel.class);

            String actualBookTitle = registrationClubResponse.bookTitle();
            assertThat(actualBookTitle).isEqualTo(bookTitle);
        });
    }
}
