package tests.clubs;

import models.clubs.deleteBookClubs.PermissionUnsuccessfulBookClubDeleteResponseModel;
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
import static org.assertj.core.api.Assertions.assertThat;

public class BookClubDeleteTests extends TestBase {
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


    @BeforeEach
    public void prepareTestData() {
        username = testData.getUsername();
        username_1 = testData.getUsername();
        password = testData.getPassword();
        password_1 = testData.getPassword();
        bookTitle = testData.getBookTitle() + "qa.guru_039 AlexKulch";
        bookAuthors = testData.getBookAuthor();
        publicationYear = testData.getPublicationYear();
        description = testData.getBookDescription();
        telegramChatLink = testData.getTelegramChatLink();


    }

    @Test
    @DisplayName("Тест на проверку удаления клуба с аккаунта не создателя клуба")
    public void successfulBookClubRegistrationTest() {

        RegistrationBodyModel registrationData = new RegistrationBodyModel(username, password);
        api.users.registration(registrationData);

        LoginBodyModel loginData = new LoginBodyModel(username, password);
        String actualAccessToken = api.auth.loginAndGetAccessToken(loginData);

        SuccessfulBookClubRegistrationBodyModel registrationClubData = new SuccessfulBookClubRegistrationBodyModel(
                bookTitle,
                bookAuthors,
                publicationYear,
                description,
                telegramChatLink);

        SuccessfulBookClubRegistrationResponseModel registrationResponse =
                step("Регистрация нового клуба и проверка ответа (201)", () ->
                        api.clubs.bookClubsRegistration(actualAccessToken, registrationClubData));

        int actualId = registrationResponse.id();

        step("Проверка соответствия полученных данными", () -> {
            assertThat(actualId).isGreaterThan(0);
            assertThat(registrationResponse.bookTitle()).isEqualTo(bookTitle);
            assertThat(registrationResponse.bookAuthors()).isEqualTo(bookAuthors);
            assertThat(registrationResponse.publicationYear()).isEqualTo(publicationYear);
            assertThat(registrationResponse.description()).isEqualTo(description);
            assertThat(registrationResponse.telegramChatLink()).isEqualTo(telegramChatLink);
        });

        RegistrationBodyModel registrationData_1 = new RegistrationBodyModel(username_1, password_1);
        api.users.registration(registrationData_1);

        LoginBodyModel loginData_1 = new LoginBodyModel(username_1, password_1);
        String actualAccessToken_1 = api.auth.loginAndGetAccessToken(loginData_1);

        PermissionUnsuccessfulBookClubDeleteResponseModel registrationResponse_1 =
                step("Удаление клуба с аккаунта не создателя клуба", () ->
                        api.clubs.bookClubPermissionDelete(actualAccessToken_1, actualId));

        step("Проверка текста ошибки в ответе", () -> {
            String actualDetail = registrationResponse_1.detail();
            String detail = "You do not have permission to perform this action.";
            assertThat(actualDetail).isEqualTo(detail);
        });

        api.clubs.bookClubDelete(actualAccessToken, actualId);
        api.users.deleteUserAuthorized(actualAccessToken);
        api.users.deleteUserAuthorized(actualAccessToken_1);

    }
}
