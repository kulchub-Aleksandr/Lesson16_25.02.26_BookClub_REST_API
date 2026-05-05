package tests.clubs;

import allure.Layer;
import models.clubs.listBookClub.BookClubsListResponseModel;
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


public class BookClubListTests extends TestBase {
    private final TestData testData = new TestData();
    private String username;
    private String password;
    private String bookTitle;
    private String bookAuthors;
    private Integer publicationYear;
    private String description;
    private String telegramChatLink;
    String search;
    String membershipOwner;
    String membershipMember;

    @BeforeEach
    public void prepareTestData() {
        username = testData.getUsername();
        password = testData.getPassword();
        bookTitle = testData.getBookTitle() + "  qa.guru_039 AlexKulch";
        bookAuthors = testData.getBookAuthor();
        publicationYear = testData.getPublicationYear();
        description = testData.getBookDescription();
        telegramChatLink = testData.getTelegramChatLink();
        search = "Сети";
        membershipOwner = "owner";
        membershipMember = "member";
    }

    @Test
    @DisplayName("Тест на получение списка клубов")
    public void getClubsListReturns200AndValidStructureTest() {

        BookClubsListResponseModel response = api.clubs.getClubsList();

        step("Проверка соответствия полученных данных в ответе", () -> {
            assertThat(response).isNotNull();
            assertThat(response.count()).isGreaterThanOrEqualTo(0);
            assertThat(response.results()).isNotNull();
        });
    }

    @Test
    @DisplayName("Тест на получение клуба по названию клуба")
    public void getClubsListBookTitleTest() {
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

        BookClubsListResponseModel response
                = api.clubs.getClubsListBookTitle(
                registrationResponseBookClub.bookTitle(),
                1,
                10);

        step("Проверка соответствия полученных данных в ответе", () -> {
            assertThat(response).isNotNull();
            assertThat(response.count()).isGreaterThanOrEqualTo(0);
            assertThat(response.results()).isNotNull();
            assertThat(response.results())
                    .as("count должно совпадать с размером results")
                    .hasSize(response.count());
            assertThat(response.results().getFirst().bookTitle())
                    .as("Название книги не совпадает с запросом в поиске")
                    .isEqualTo(registrationResponseBookClub.bookTitle());
        });
    }

    @Test
    @DisplayName("Тест фильтрации списка клубов: поиск по названию и членству, без авторизации юзера")
    public void getClubsListBookTitleMembershipTest() {

        BookClubsListResponseModel response
                = api.clubs.getClubsBookClubsBookTitleMembershipList(bookTitle, 1, 10, membershipOwner);

        step("Проверка соответствия полученных данных в ответе", () -> {
            assertThat(response).isNotNull();
            assertThat(response.count()).isEqualTo(0);
            assertThat(response.results()).isNotNull();
        });
    }

    @Test
    @DisplayName("Тест фильтрации списка клубов: поиск по членству, без авторизации юзера")
    public void getClubsListMembershipTest() {

        BookClubsListResponseModel response
                = api.clubs.getClubsBookClubsMembershipList(1, 10, membershipOwner);

        step("Проверка соответствия полученных данных в ответе", () -> {
            assertThat(response).isNotNull();
            assertThat(response.count()).isEqualTo(0);
            assertThat(response.results()).isNotNull();

        });
    }

    @Test
    @DisplayName("Тест фильтрации списка клубов: поиск по членству, с авторизованным пользователем, без создания клуба")
    public void getClubsListMembershipWithAnAuthorizedUserTest() {

        SuccessfulRegistrationResponseModel registrationResponse
                = api.users.registration(new RegistrationBodyModel(username, password));

        step("Проверка соответствия отправленных данных с данными в ответе", () -> {
            assertThat(registrationResponse.username()).isEqualTo(username);
        });

        String actualAccessToken = api.auth.loginAndGetAccessToken(new LoginBodyModel(username, password));

        BookClubsListResponseModel response
                = api.clubs.getClubsBookClubsMembershipList(1, 10, membershipOwner);

        step("Проверка соответствия полученных данных в ответе", () -> {
            assertThat(response).isNotNull();
            assertThat(response.count()).isEqualTo(0);
            assertThat(response.results()).isNotNull();
        });

        api.users.deleteUserAuthorized(actualAccessToken);
    }

    @Test
    @DisplayName("Тест фильтрации списка клубов: поиск по членству Owner, с авторизованным пользователем, с созданием клуба")
    public void getClubsListMembershipOwnerWithAnAuthorizedUserCreatingClubTest() {

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

        BookClubsListResponseModel response
                = api.clubs.getClubsBookClubsMembershipListOwner(
                actualAccessToken, 1, 10, membershipOwner);

        step("Проверка соответствия полученных данных в ответе", () -> {
            assertThat(response).isNotNull();
            assertThat(response.count()).isGreaterThanOrEqualTo(0);
            assertThat(response.results()).isNotNull();
            assertThat(response.results()).hasSize(1);
            assertThat(response.results().getFirst().bookTitle())
                    .as("Название книги не совпадает с запросом в поиске")
                    .isEqualTo(bookTitle);
            assertThat(response.results().getFirst().bookAuthors()).isEqualTo(bookAuthors);
            assertThat(response.results().getFirst().publicationYear()).isEqualTo(publicationYear);
            assertThat(response.results().getFirst().description()).isEqualTo(description);
            assertThat(response.results().getFirst().telegramChatLink()).isEqualTo(telegramChatLink);
            assertThat(response.results().getFirst().owner()).isEqualTo(registrationResponseBookClub.owner());
        });

        api.clubs.bookClubDelete(actualAccessToken, registrationResponseBookClub.id());
        api.users.deleteUserAuthorized(actualAccessToken);
    }

    @Test
    @DisplayName("Тест фильтрации списка клубов: поиск по членству Member, с авторизованным пользователем, с созданием клуба")
    public void getClubsListMembershipMemberWithAnAuthorizedUserCreatingClubTest() {

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

        BookClubsListResponseModel response
                = api.clubs.getClubsBookClubsMembershipListOwner(actualAccessToken, 1, 10, membershipMember);

        step("Проверка соответствия полученных данных в ответе", () -> {
            assertThat(response).isNotNull();
            assertThat(response.count()).isGreaterThanOrEqualTo(0);
            assertThat(response.results()).isNotNull();
            assertThat(response.results()).hasSize(1);
            assertThat(response.results().getFirst().bookTitle())
                    .as("Название книги не совпадает с запросом в поиске")
                    .isEqualTo(bookTitle);
            assertThat(response.results().getFirst().bookAuthors()).isEqualTo(bookAuthors);
            assertThat(response.results().getFirst().publicationYear()).isEqualTo(publicationYear);
            assertThat(response.results().getFirst().description()).isEqualTo(description);
            assertThat(response.results().getFirst().telegramChatLink()).isEqualTo(telegramChatLink);
            assertThat(response.results().getFirst().owner()).isEqualTo(registrationResponseBookClub.owner());


        });

        api.clubs.bookClubDelete(actualAccessToken, registrationResponseBookClub.id());
        api.users.deleteUserAuthorized(actualAccessToken);
    }

    @Test
    @DisplayName("Тест на получение клуба по ID , с авторизованным пользователем, с созданием клуба")
    public void getClubsListByIdWithAnAuthorizedUserCreatingClubTest() {

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
            assertThat(registrationResponseBookClub.id()).isGreaterThan(0);
            assertThat(registrationResponseBookClub.owner()).isGreaterThan(0);
            assertThat(registrationResponseBookClub.bookTitle()).isEqualTo(bookTitle);
        });

        SuccessfulBookClubRegistrationResponseModel response
                = api.clubs.getClubById(actualAccessToken, registrationResponseBookClub.id());

        step("Проверка соответствия полученных данных в ответе", () -> {
            assertThat(response.id()).isGreaterThan(0);
            assertThat(response.owner()).isGreaterThan(0);
            assertThat(response.bookTitle()).isEqualTo(bookTitle);
            assertThat(response.bookAuthors()).isEqualTo(bookAuthors);
            assertThat(response.publicationYear()).isEqualTo(publicationYear);
            assertThat(response.description()).isEqualTo(description);
            assertThat(response.telegramChatLink()).isEqualTo(telegramChatLink);
        });

        api.clubs.bookClubDelete(actualAccessToken, registrationResponseBookClub.id());
        api.users.deleteUserAuthorized(actualAccessToken);
    }
}
