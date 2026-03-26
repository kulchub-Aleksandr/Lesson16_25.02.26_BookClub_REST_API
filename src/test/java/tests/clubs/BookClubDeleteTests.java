package tests.clubs;

import models.clubs.deleteBookClub.PermissionUnsuccessfulBookClubDeleteResponseModel;
import models.clubs.registrationBookClub.SuccessfulBookClubRegistrationBodyModel;
import models.clubs.registrationBookClub.SuccessfulBookClubRegistrationResponseModel;
import models.users.login.LoginBodyModel;
import models.users.registration.RegistrationBodyModel;
import models.users.registration.SuccessfulRegistrationResponseModel;
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
        bookTitle = testData.getBookTitle() + "  qa.guru_039 AlexKulch";
        bookAuthors = testData.getBookAuthor();
        publicationYear = testData.getPublicationYear();
        description = testData.getBookDescription();
        telegramChatLink = testData.getTelegramChatLink();


    }

    @Test
    @DisplayName("Тест на проверку удаления клуба")
    public void successfulBookClubDeleteTest() {

        SuccessfulRegistrationResponseModel registrationResponse_1 =
                step("Регистрация нового пользователя", () -> {
                    RegistrationBodyModel registrationData = new RegistrationBodyModel(username, password);
                    return api.users.registration(registrationData);
                });

        step("Проверка соответствия отправленных данных с данными в ответе", () -> {
            assertThat(registrationResponse_1.username()).isEqualTo(username);
        });

        String actualAccessToken = step("Авторизация и получение access-токена", () -> {
            LoginBodyModel loginData = new LoginBodyModel(username, password);
            return api.auth.loginAndGetAccessToken(loginData);
        });

        SuccessfulBookClubRegistrationResponseModel registrationResponse =
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

    @Test
    @DisplayName("Тест на проверку удаления клуба с аккаунта не создателя клуба")
    public void permissionUnsuccessfulBookClubDeleteTest() {

        SuccessfulRegistrationResponseModel registrationResponse =
                step("Регистрация нового пользователя", () -> {
                    RegistrationBodyModel registrationData = new RegistrationBodyModel(username, password);
                    return api.users.registration(registrationData);
                });

        step("Проверка соответствия отправленных данных с данными в ответе", () -> {
            assertThat(registrationResponse.username()).isEqualTo(username);
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
            int idFromResponse = registrationResponseBookClub.id();
            assertThat(idFromResponse).isGreaterThan(0);
            assertThat(registrationResponseBookClub.bookTitle()).isEqualTo(bookTitle);
            assertThat(registrationResponseBookClub.bookAuthors()).isEqualTo(bookAuthors);
            assertThat(registrationResponseBookClub.publicationYear()).isEqualTo(publicationYear);
            assertThat(registrationResponseBookClub.description()).isEqualTo(description);
            assertThat(registrationResponseBookClub.telegramChatLink()).isEqualTo(telegramChatLink);
        });

        SuccessfulRegistrationResponseModel registrationResponse_1 =
                step("Регистрация нового пользователя", () -> {
                    RegistrationBodyModel registrationData_1 = new RegistrationBodyModel(username_1, password_1);
                    return api.users.registration(registrationData_1);
                });

        step("Проверка соответствия отправленных данных с данными в ответе", () -> {
            assertThat(registrationResponse_1.username()).isEqualTo(username_1);
        });

        String actualAccessToken_1 = step("Авторизация и получение access-токена", () -> {

            LoginBodyModel loginData_1 = new LoginBodyModel(username_1, password_1);
            return api.auth.loginAndGetAccessToken(loginData_1);
        });

        PermissionUnsuccessfulBookClubDeleteResponseModel registrationResponse_2 =
                step("Удаление клуба с аккаунта не создателя клуба", () ->
                        api.clubs.bookClubPermissionDelete(actualAccessToken_1, registrationResponseBookClub.id()));

        step("Проверка текста ошибки в ответе", () -> {
            String actualDetail = registrationResponse_2.detail();
            String detail = "You do not have permission to perform this action.";
            assertThat(actualDetail).isEqualTo(detail);
        });

        api.clubs.bookClubDelete(actualAccessToken, registrationResponseBookClub.id());
        api.users.deleteUserAuthorized(actualAccessToken);
        api.users.deleteUserAuthorized(actualAccessToken_1);

    }
}
