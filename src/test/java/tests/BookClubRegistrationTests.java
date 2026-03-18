package tests;

import models.bookClubRegistration.BookClubRegistrationBodyModel;
import models.bookClubRegistration.SuccessfulBookClubRegistrationResponseModel;
import models.login.LoginBodyModel;
import models.registration.RegistrationBodyModel;
import models.registration.SuccessfulRegistrationResponseModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.qameta.allure.Allure.step;
import static io.restassured.RestAssured.given;
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
        bookTitle = testData.getBookTitle() + "qa.guru_039";
        bookAuthors = testData.getBookAuthor();
        publicationYear = testData.getPublicationYear();
        description = testData.getBookDescription();
        telegramChatLink = testData.getTelegramChatLink();


    }

    @Test
    @DisplayName("Тест на проверку регистрации нового клуба")
    public void successfulBookClubRegistrationTest() {
        SuccessfulRegistrationResponseModel registrationResponse = step("Регистрация нового пользователя", () -> {
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


        String actualAccessToken = step("Авторизация и получение access-токена", () -> {
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


        SuccessfulBookClubRegistrationResponseModel registrationClubResponse =
                step("Регистрация нового клуба  и проверка ответа (201)", () -> {
                    BookClubRegistrationBodyModel registrationClubData = new BookClubRegistrationBodyModel(
                            bookTitle,
                            bookAuthors,
                            publicationYear,
                            description,
                            telegramChatLink);

                    return given(bookClubRegistrationRequestSpec)
                            .header("Authorization", "Bearer " + actualAccessToken)
                            .body(registrationClubData)
                            .when()
                            .post("/clubs/")
                            .then()
                            .spec(bookClubSuccessfulRegistrationResponseSpec)
                            .extract()
                            .as(SuccessfulBookClubRegistrationResponseModel.class);

                });
        step("Проверка соответствия отправленных данных с данными в ответе", () -> {
            assertThat(registrationClubResponse.bookTitle()).isEqualTo(bookTitle);
            assertThat(registrationClubResponse.bookAuthors()).isEqualTo(bookAuthors);
            assertThat(registrationClubResponse.publicationYear()).isEqualTo(publicationYear);
            assertThat(registrationClubResponse.description()).isEqualTo(description);
            assertThat(registrationClubResponse.telegramChatLink()).isEqualTo(telegramChatLink);
        });
    }
}
