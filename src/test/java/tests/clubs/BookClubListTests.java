package tests.clubs;

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
    @DisplayName("Тест на получение списка клубов")
    public void getClubsListReturns200AndValidStructureTest() {

        BookClubsListResponseModel response = api.clubs.getClubsList();

        step("Проверка соответствия полученных данных в ответе", () -> {
            assertThat(response).isNotNull();
            assertThat(response.count()).isGreaterThanOrEqualTo(0);
            assertThat(response.results()).isNotNull();
            assertThat(response.results()).hasSize(response.count());

        });
    }

    @Test
    @DisplayName("Тест на получение клуба по названию клуба")
    public void getClubsListBookTitleTest() {

        BookClubsListResponseModel response =
                step("Тест на получение клуба по названию клуба \"Сети\"", () -> {
                    String search = "Сети";
                    return api.clubs.getClubsListBookTitle(search, 1, 10);
                });

        step("Проверка соответствия полученных данных в ответе", () -> {
            assertThat(response).isNotNull();
            assertThat(response.count()).isGreaterThanOrEqualTo(0);
            assertThat(response.results()).isNotNull();
            assertThat(response.results())
                    .as("count должно совпадать с размером results")
                    .hasSize(response.count());
            assertThat(response.results().getFirst().bookTitle())
                    .as("Название книги не совпадает с запросом в поиске")
                    .isEqualTo("Сети");

        });
    }

    @Test
    @DisplayName("Тест фильтрации списка клубов: поиск по названию и членству, без авторизации юзера")
    public void getClubsListBookTitleMembershipTest() {

        BookClubsListResponseModel response =
                step("Тест на получение клуба по названию клуба \"Сети\" и membership=owner", () -> {
                    String search = "Сети";
                    String membership = "owner";
                    return api.clubs.getClubsBookClubsBookTitleMembershipList(search, 1, 10, membership);
                });

        step("Проверка соответствия полученных данных в ответе", () -> {
            assertThat(response).isNotNull();
            assertThat(response.count()).isGreaterThanOrEqualTo(0);
            assertThat(response.results()).isNotNull();

        });
    }

    @Test
    @DisplayName("Тест фильтрации списка клубов: поиск по членству, без авторизации юзера")
    public void getClubsListMembershipTest() {

        BookClubsListResponseModel response =
                step("Тест на получение клуба по  membership=owner", () -> {

                    String membership = "owner";
                    return api.clubs.getClubsBookClubsMembershipList(1, 10, membership);
                });

        step("Проверка соответствия полученных данных в ответе", () -> {
            assertThat(response).isNotNull();
            assertThat(response.count()).isGreaterThanOrEqualTo(0);
            assertThat(response.results()).isNotNull();

        });
    }

    @Test
    @DisplayName("Тест фильтрации списка клубов: поиск по членству, с авторизованным пользователем, без создания клуба")
    public void getClubsListMembershipWithAnAuthorizedUserTest() {

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

        BookClubsListResponseModel response =
                step("Тест на получение клуба по  membership=owner", () -> {

            String membership = "owner";
            return api.clubs.getClubsBookClubsMembershipList(1, 10, membership);
        });

        step("Проверка соответствия полученных данных в ответе", () -> {
            assertThat(response).isNotNull();
            assertThat(response.count()).isGreaterThanOrEqualTo(0);
            assertThat(response.results()).isNotNull();

        });

//        api.clubs.bookClubDelete(actualAccessToken, registrationResponse.id());
        api.users.deleteUserAuthorized(actualAccessToken);
    }

    @Test
    @DisplayName("Тест фильтрации списка клубов: поиск по членству, с авторизованным пользователем, с созданием клуба")
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
                    SuccessfulBookClubRegistrationResponseModel response =
                            api.clubs.bookClubsRegistration(actualAccessToken, registrationClubData);
                    return response;
                });
        step("Проверка соответствия полученных данных в ответе", () -> {
            int ownerFromResponse = registrationResponseBookClub.owner();
            assertThat(registrationResponseBookClub.id()).isGreaterThan(0);;
            assertThat(registrationResponseBookClub.bookTitle()).isEqualTo(bookTitle);
            assertThat(registrationResponseBookClub.bookAuthors()).isEqualTo(bookAuthors);
            assertThat(registrationResponseBookClub.publicationYear()).isEqualTo(publicationYear);
            assertThat(registrationResponseBookClub.description()).isEqualTo(description);
            assertThat(registrationResponseBookClub.telegramChatLink()).isEqualTo(telegramChatLink);
        });

        BookClubsListResponseModel response =
                step("Тест на получение клуба по  membership=owner", () -> {

            String membership = "owner";
            return api.clubs.getClubsBookClubsMembershipListOwner(actualAccessToken,1, 10, membership);
        });

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
           // assertThat(response.results().getFirst().owner()).isEqualTo(ownerFromResponse);


        });

        api.clubs.bookClubDelete(actualAccessToken, registrationResponseBookClub.id());
        api.users.deleteUserAuthorized(actualAccessToken);
    }




}
