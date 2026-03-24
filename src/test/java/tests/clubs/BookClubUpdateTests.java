package tests.clubs;

import models.clubs.listBookClub.BookClubsListResponseModel;
import models.clubs.registrationBookClub.SuccessfulBookClubRegistrationBodyModel;
import models.clubs.registrationBookClub.SuccessfulBookClubRegistrationResponseModel;
import models.users.login.LoginBodyModel;
import models.users.registration.RegistrationBodyModel;
import models.users.registration.SuccessfulRegistrationResponseModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import tests.TestBase;
import tests.TestData;

import static io.qameta.allure.Allure.step;
import static org.assertj.core.api.Assertions.assertThat;

public class BookClubUpdateTests extends TestBase {
    private final TestData testData = new TestData();
    private String username;
    private String username_1;
    private String password;
    private String password_1;

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
        username_1 = testData.getUsername();
        password = testData.getPassword();
        password_1 = testData.getPassword();

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
    @DisplayName("Тест изменение всех данных клуба, с авторизованным пользователем, с созданием клуба")
    public void getClubsListMembershipWithAnAuthorizedUserCreatingClubTest() {

        SuccessfulRegistrationResponseModel registrationUserResponse =
                step("Регистрация нового пользователя", () -> {
                    RegistrationBodyModel registrationData = new RegistrationBodyModel(username, password);
                    return api.users.registration(registrationData);
                });

        step("Проверка соответствия отправленных данных с данными в ответе", () -> {
            assertThat(registrationUserResponse.username()).isEqualTo(username);
        });

        String actualAccessToken = step("Авторизация и получение access-токена", () -> {
            LoginBodyModel loginData = new LoginBodyModel(username, password);
            return api.auth.loginAndGetAccessToken(loginData);
        });

        SuccessfulBookClubRegistrationResponseModel registrationResponseBookClub =
                step("Регистрация нового клуба и проверка ответа (201)", () -> {
                    SuccessfulBookClubRegistrationBodyModel registrationClubData = new SuccessfulBookClubRegistrationBodyModel(
                            bookTitle,
                            bookAuthors,
                            publicationYear,
                            description,
                            telegramChatLink);
                    return api.clubs.bookClubsRegistration(actualAccessToken, registrationClubData);
                });
        step("Проверка соответствия полученных данных в ответе", () -> {
            assertThat(registrationResponseBookClub.id()).isGreaterThan(0);
            assertThat(registrationResponseBookClub.owner()).isGreaterThan(0);
            assertThat(registrationResponseBookClub.bookTitle()).isEqualTo(bookTitle);
            assertThat(registrationResponseBookClub.bookAuthors()).isEqualTo(bookAuthors);
            assertThat(registrationResponseBookClub.publicationYear()).isEqualTo(publicationYear);
            assertThat(registrationResponseBookClub.description()).isEqualTo(description);
            assertThat(registrationResponseBookClub.telegramChatLink()).isEqualTo(telegramChatLink);
        });

        SuccessfulBookClubRegistrationResponseModel updateBookClubResponse =
                step("Внесение изменений в данные клуба", () -> {
                    SuccessfulBookClubRegistrationBodyModel registrationClubData = new SuccessfulBookClubRegistrationBodyModel(
                            bookTitle_1,
                            bookAuthors_1,
                            publicationYear_1,
                            description_1,
                            telegramChatLink_1);
                    return api.clubs.bookClubsUpdate(actualAccessToken, registrationClubData,registrationResponseBookClub.id());
                });

        step("Проверка соответствия полученных данных в ответе", () -> {
            assertThat(updateBookClubResponse.id()).isGreaterThan(0);
            assertThat(updateBookClubResponse.owner()).isGreaterThan(0);
            assertThat(updateBookClubResponse.bookTitle()).isEqualTo(bookTitle_1);
            assertThat(updateBookClubResponse.bookAuthors()).isEqualTo(bookAuthors_1);
            assertThat(updateBookClubResponse.publicationYear()).isEqualTo(publicationYear_1);
            assertThat(updateBookClubResponse.description()).isEqualTo(description_1);
            assertThat(updateBookClubResponse.telegramChatLink()).isEqualTo(telegramChatLink_1);

        });

        SuccessfulBookClubRegistrationResponseModel response =
                step("Тест на получение клуба по ID и проверка что данные изменились", () -> {

                    return api.clubs.getClubByIdAfterPut(actualAccessToken, registrationResponseBookClub.id());
                });

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
}
