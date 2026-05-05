package tests.clubs;

import io.qameta.allure.Story;
import models.clubs.registrationBookClub.SuccessfulBookClubRegistrationBodyModel;
import models.clubs.registrationBookClub.SuccessfulBookClubRegistrationResponseModel;
import models.clubs.updateBookClub.SuccessfulBookClubUpdateBodyModel;
import models.users.login.LoginBodyModel;
import models.users.registration.RegistrationBodyModel;
import models.users.registration.SuccessfulRegistrationResponseModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import tests.TestBase;
import tests.TestData;

import static io.qameta.allure.Allure.step;
import static org.assertj.core.api.Assertions.assertThat;


public class BookClubUpdateTests extends TestBase {
    private final TestData testData = new TestData();
    private String username;
    private String password;

    private String bookTitle;
    private String bookAuthors;
    private Integer publicationYear;
    private String description;
    private String telegramChatLink;

    private String bookTitle_1;
    private String bookAuthors_1;
    private Integer publicationYear_1;
    private String description_1;
    private String telegramChatLink_1;


    @BeforeEach
    public void prepareTestData() {
        username = testData.getUsername();
        password = testData.getPassword();

        bookTitle = testData.getBookTitle() + "  qa.guru_039 AlexKulch";
        bookAuthors = testData.getBookAuthor();
        publicationYear = testData.getPublicationYear();
        description = testData.getBookDescription();
        telegramChatLink = testData.getTelegramChatLink();

        bookTitle_1 = testData.getBookTitle() + "  qa.guru_039 AlexKulch + 1";
        bookAuthors_1 = testData.getBookAuthor();
        publicationYear_1 = testData.getPublicationYear();
        description_1 = testData.getBookDescription();
        telegramChatLink_1 = testData.getTelegramChatLink();
    }

    @Test
    @Story("Изменение данных клуба")
    @Tag("API")
    @DisplayName("Тест на изменение всех данных клуба методом PUT, с авторизованным пользователем, с созданием клуба")
    public void updateClubWithAnAuthorizedUserCreatingClubTest() {

        SuccessfulRegistrationResponseModel registrationResponse
                = api.users.registration(new RegistrationBodyModel(username, password));

        step("Проверка соответствия отправленных данных с данными в ответе", () -> {
            assertThat(registrationResponse.username()).isEqualTo(username);
        });

        String actualAccessToken = api.auth.loginAndGetAccessToken(new LoginBodyModel(username, password));

        SuccessfulBookClubRegistrationResponseModel registrationResponseBookClub
                = api.clubs.bookClubsRegistration(actualAccessToken, new SuccessfulBookClubRegistrationBodyModel(
                bookTitle,
                bookAuthors,
                publicationYear,
                description,
                telegramChatLink));

        step("Проверка соответствия полученных данных в ответе", () -> {
            assertThat(registrationResponseBookClub.bookTitle()).isEqualTo(bookTitle);
            assertThat(registrationResponseBookClub.bookAuthors()).isEqualTo(bookAuthors);
        });

        SuccessfulBookClubRegistrationResponseModel updateBookClubResponse =
                api.clubs.bookClubsUpdatePutMethod(actualAccessToken, new SuccessfulBookClubRegistrationBodyModel(
                        bookTitle_1,
                        bookAuthors_1,
                        publicationYear_1,
                        description_1,
                        telegramChatLink_1), registrationResponseBookClub.id());

        step("Проверка соответствия полученных данных в ответе", () -> {
            assertThat(updateBookClubResponse.id()).isGreaterThan(0);
            assertThat(updateBookClubResponse.owner()).isGreaterThan(0);
            assertThat(updateBookClubResponse.bookTitle()).isEqualTo(bookTitle_1);
            assertThat(updateBookClubResponse.bookAuthors()).isEqualTo(bookAuthors_1);
            assertThat(updateBookClubResponse.publicationYear()).isEqualTo(publicationYear_1);
            assertThat(updateBookClubResponse.description()).isEqualTo(description_1);
            assertThat(updateBookClubResponse.telegramChatLink()).isEqualTo(telegramChatLink_1);
        });

        SuccessfulBookClubRegistrationResponseModel response
                = api.clubs.getClubByIdAfterPut(actualAccessToken, registrationResponseBookClub.id());


        step("Проверка соответствия полученных данных в ответе", () -> {
            assertThat(response.id()).isGreaterThan(0);
            assertThat(response.owner()).isGreaterThan(0);
            assertThat(response.bookTitle()).isEqualTo(bookTitle_1);
            assertThat(response.bookAuthors()).isEqualTo(bookAuthors_1);
            assertThat(response.publicationYear()).isEqualTo(publicationYear_1);
            assertThat(response.description()).isEqualTo(description_1);
            assertThat(response.telegramChatLink()).isEqualTo(telegramChatLink_1);
        });

        api.clubs.bookClubDelete(actualAccessToken, registrationResponseBookClub.id());
        api.users.deleteUserAuthorized(actualAccessToken);
    }

    @Test
    @Story("Изменение данных клуба")
    @Tag("API")
    @DisplayName("Тест на изменение частичных данных клуба методом PATCH, с авторизованным пользователем, с созданием клуба")
    public void partialUpdateClubWithAnAuthorizedUserCreatingClubTest() {

        SuccessfulRegistrationResponseModel registrationResponse
                = api.users.registration(new RegistrationBodyModel(username, password));

        step("Проверка соответствия отправленных данных с данными в ответе", () -> {
            assertThat(registrationResponse.username()).isEqualTo(username);
        });

        String actualAccessToken = api.auth.loginAndGetAccessToken(new LoginBodyModel(username, password));

        SuccessfulBookClubRegistrationResponseModel registrationResponseBookClub
                = api.clubs.bookClubsRegistration(actualAccessToken, new SuccessfulBookClubRegistrationBodyModel(
                bookTitle,
                bookAuthors,
                publicationYear,
                description,
                telegramChatLink));

        step("Проверка соответствия полученных данных в ответе", () -> {
            assertThat(registrationResponseBookClub.bookTitle()).isEqualTo(bookTitle);
            assertThat(registrationResponseBookClub.bookAuthors()).isEqualTo(bookAuthors);
        });

        SuccessfulBookClubRegistrationResponseModel updateBookClubResponse
                = api.clubs.bookClubsPartialUpdatePatchMethod(actualAccessToken,
                new SuccessfulBookClubUpdateBodyModel(
                        publicationYear_1,
                        description_1),
                registrationResponseBookClub.id());

        step("Проверка соответствия полученных данных в ответе", () -> {
            assertThat(updateBookClubResponse.id()).isGreaterThan(0);
            assertThat(updateBookClubResponse.owner()).isGreaterThan(0);
            assertThat(updateBookClubResponse.bookTitle()).isEqualTo(bookTitle);
            assertThat(updateBookClubResponse.bookAuthors()).isEqualTo(bookAuthors);
            assertThat(updateBookClubResponse.publicationYear()).isEqualTo(publicationYear_1);
            assertThat(updateBookClubResponse.description()).isEqualTo(description_1);
            assertThat(updateBookClubResponse.telegramChatLink()).isEqualTo(telegramChatLink);
        });

        SuccessfulBookClubRegistrationResponseModel response
                = api.clubs.getClubByIdAfterPut(actualAccessToken, registrationResponseBookClub.id());


        step("Проверка соответствия полученных данных в ответе", () -> {
            assertThat(response.id()).isGreaterThan(0);
            assertThat(response.owner()).isGreaterThan(0);
            assertThat(response.bookTitle()).isEqualTo(bookTitle);
            assertThat(response.bookAuthors()).isEqualTo(bookAuthors);
            assertThat(response.publicationYear()).isEqualTo(publicationYear_1);
            assertThat(response.description()).isEqualTo(description_1);
            assertThat(response.telegramChatLink()).isEqualTo(telegramChatLink);
        });

        api.clubs.bookClubDelete(actualAccessToken, registrationResponseBookClub.id());
        api.users.deleteUserAuthorized(actualAccessToken);
    }
}
