package tests.clubs;

import models.clubs.registrationBookClub.SuccessfulBookClubRegistrationBodyModel;
import models.clubs.registrationBookClub.SuccessfulBookClubRegistrationResponseModel;
import models.users.login.LoginBodyModel;
import models.users.registration.RegistrationBodyModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import tests.TestBase;
import tests.TestData;

import static io.qameta.allure.Allure.step;
import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

public class BookClubRegistrationTests extends TestBase {
    private final TestData testData = new TestData();
    private String username;
    private String password;

    private String bookTitle;
    private String bookAuthors;
    private Integer publicationYear;
    private String description;
    private String telegramChatLink;


    @BeforeEach
    public void prepareTestData() {
        username = testData.getUsername();
        password = testData.getPassword();
        bookTitle = testData.getBookTitle() + "  qa.guru_039 AlexKulch";
        bookAuthors = testData.getBookAuthor();
        publicationYear = testData.getPublicationYear();
        description = testData.getBookDescription();
        telegramChatLink = testData.getTelegramChatLink();
    }

    @Test
    @DisplayName("Тест на проверку регистрации нового клуба")
    public void successfulBookClubRegistrationTest() {

        RegistrationBodyModel registrationData = new RegistrationBodyModel(username, password);
        api.users.registration(registrationData);

        LoginBodyModel loginData = new LoginBodyModel(username, password);
        String actualAccessToken = api.auth.loginAndGetAccessToken(loginData);


       // int actualId = 0;
        SuccessfulBookClubRegistrationResponseModel registrationResponse =
                step("Регистрация нового клуба и проверка ответа (201)", () -> {
                    SuccessfulBookClubRegistrationBodyModel registrationClubData = new SuccessfulBookClubRegistrationBodyModel(
                            bookTitle,
                            bookAuthors,
                            publicationYear,
                            description,
                            telegramChatLink);
                    SuccessfulBookClubRegistrationResponseModel response =
                            api.clubs.bookClubsRegistration(actualAccessToken, registrationClubData);

                   // actualId = response.id();
                    return response;
                });
        step("Проверка соответствия полученных данных в ответе", () -> {
            int idFromResponse = registrationResponse.id();
            assertThat(idFromResponse).isGreaterThan(0);
            assertThat(registrationResponse.bookTitle()).isEqualTo(bookTitle);
            assertThat(registrationResponse.bookAuthors()).isEqualTo(bookAuthors);
            assertThat(registrationResponse.publicationYear()).isEqualTo(publicationYear);
            assertThat(registrationResponse.description()).isEqualTo(description);
            assertThat(registrationResponse.telegramChatLink()).isEqualTo(telegramChatLink);
        });

        api.clubs.bookClubDelete(actualAccessToken, registrationResponse.id());
        api.users.deleteUserAuthorized(actualAccessToken);

    }
}
