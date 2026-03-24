package tests.clubs;

import models.clubs.registrationBookClub.SuccessfulBookClubRegistrationBodyModel;
import models.clubs.registrationBookClub.SuccessfulBookClubRegistrationResponseModel;
import models.clubs.reviewsBookClub.SuccessfulReviewsGetBookClubResponseModel;
import models.clubs.reviewsBookClub.SuccessfulReviewsPostBookClubBodyModel;
import models.clubs.reviewsBookClub.SuccessfulReviewsPostBookClubResponseModel;
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

public class BookClubReviewsTests extends TestBase {
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

    private String newReview;


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

        newReview = "Пробный отзыв";
    }


    @Test
    @DisplayName("Тест на оставление отзыва на книгу, с авторизованным пользователем, с созданием клуба")
    public void bookClubReviewsPostWithAnAuthorizedUserCreatingClubTest() {

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

        SuccessfulRegistrationResponseModel registrationUserResponse_1 =
                step("Регистрация второго пользователя", () -> {
                    RegistrationBodyModel registrationData_1 = new RegistrationBodyModel(username_1, password_1);
                    return api.users.registration(registrationData_1);
                });

        step("Проверка соответствия отправленных данных с данными в ответе", () -> {
            assertThat(registrationUserResponse_1.username()).isEqualTo(username_1);
        });

        String actualAccessToken_1 = step("Авторизация и получение access-токена второго пользователя", () -> {
            LoginBodyModel loginData_1 = new LoginBodyModel(username_1, password_1);
            return api.auth.loginAndGetAccessToken(loginData_1);
        });

        step("Регистрация нового члена клуба", () -> {
            api.clubs.bookClubMemberRegistration(actualAccessToken_1, registrationResponseBookClub.id());
        });

        SuccessfulBookClubRegistrationResponseModel response =
                step("Тест на получение информации клуба по ID и проверка что данные изменились", () -> {
                    return api.clubs.getClubById(actualAccessToken_1, registrationResponseBookClub.id());
                });

        step("Проверка что в члены клуба добавился второй пользователь", () -> {
            assertThat(response.id()).isGreaterThan(0);
            assertThat(response.owner()).isGreaterThan(0);
            assertThat(response.members()).contains(response.owner());
            assertThat(response.members()).contains(registrationUserResponse_1.id());
        });

        SuccessfulReviewsPostBookClubResponseModel reviewsResponse =
                step("Публикация отзыва на книгу", () -> {
                    SuccessfulReviewsPostBookClubBodyModel reviewsData =
                            new SuccessfulReviewsPostBookClubBodyModel(response.id(), newReview, 5, 22);
                    return api.clubs.bookClubReviewsPost(actualAccessToken_1, reviewsData);
                });

        step("Проверка что отзыв второго пользователя добавился ", () -> {
            assertThat(reviewsResponse.id()).isGreaterThan(0);
            assertThat(reviewsResponse.club()).isGreaterThan(0);
            assertThat(reviewsResponse.user().id()).isEqualTo(registrationUserResponse_1.id());
            assertThat(reviewsResponse.user().username()).isEqualTo(registrationUserResponse_1.username());
            assertThat(reviewsResponse.review()).isEqualTo(newReview);
        });

        api.clubs.bookClubDelete(actualAccessToken, registrationResponseBookClub.id());
        api.users.deleteUserAuthorized(actualAccessToken);
        api.users.deleteUserAuthorized(actualAccessToken_1);

    }


    @Test
    @DisplayName("Тест на вызов отзывов на книгу, с авторизованным пользователем, с созданием клуба")
    public void getReviewsBookClubWithAnAuthorizedUserCreatingClubTest() {

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

        SuccessfulRegistrationResponseModel registrationUserResponse_1 =
                step("Регистрация второго пользователя", () -> {
                    RegistrationBodyModel registrationData_1 = new RegistrationBodyModel(username_1, password_1);
                    return api.users.registration(registrationData_1);
                });

        step("Проверка соответствия отправленных данных с данными в ответе", () -> {
            assertThat(registrationUserResponse_1.username()).isEqualTo(username_1);
        });

        String actualAccessToken_1 = step("Авторизация и получение access-токена второго пользователя", () -> {
            LoginBodyModel loginData_1 = new LoginBodyModel(username_1, password_1);
            return api.auth.loginAndGetAccessToken(loginData_1);
        });

        step("Регистрация нового члена клуба", () -> {
            api.clubs.bookClubMemberRegistration(actualAccessToken_1, registrationResponseBookClub.id());
        });

        SuccessfulBookClubRegistrationResponseModel response =
                step("Тест на получение информации клуба по ID и проверка что данные изменились", () -> {
                    return api.clubs.getClubById(actualAccessToken_1, registrationResponseBookClub.id());
                });

        step("Проверка что в члены клуба добавился второй пользователь", () -> {
            assertThat(response.id()).isGreaterThan(0);
            assertThat(response.owner()).isGreaterThan(0);
            assertThat(response.members()).contains(response.owner());
            assertThat(response.members()).contains(registrationUserResponse_1.id());
        });

        SuccessfulReviewsPostBookClubResponseModel reviewsResponse =
                step("Публикация отзыва на книгу", () -> {
                    SuccessfulReviewsPostBookClubBodyModel reviewsData =
                            new SuccessfulReviewsPostBookClubBodyModel(response.id(), newReview, 5, 22);
                    return api.clubs.bookClubReviewsPost(actualAccessToken_1, reviewsData);
                });

        step("Проверка что отзыв второго пользователя добавился ", () -> {
            assertThat(reviewsResponse.id()).isGreaterThan(0);
            assertThat(reviewsResponse.club()).isGreaterThan(0);
            assertThat(reviewsResponse.user().id()).isEqualTo(registrationUserResponse_1.id());
            assertThat(reviewsResponse.user().username()).isEqualTo(registrationUserResponse_1.username());
            assertThat(reviewsResponse.review()).isEqualTo(newReview);
        });

        SuccessfulReviewsGetBookClubResponseModel reviewsListResponse =
                step("Запрос списка отзывов  ", () -> {
//                    SuccessfulReviewsPostBookClubBodyModel reviewsData =
//                            new SuccessfulReviewsPostBookClubBodyModel(response.id(), newReview, 5, 22);
                    return api.clubs.getReviewsBookClub(registrationResponseBookClub.id(), 1, 100);
                });
        step("Проверка что отзывы видны ", () -> {
            assertThat(reviewsListResponse.count()).isGreaterThan(0);
            assertThat(reviewsListResponse.results().getFirst().user().id()).isEqualTo(registrationUserResponse_1.id());
            assertThat(reviewsListResponse.results().getFirst().user().username()).isEqualTo(registrationUserResponse_1.username());
            assertThat(reviewsListResponse.results().getFirst().review()).isEqualTo(newReview);//
        });


        api.clubs.bookClubDelete(actualAccessToken, registrationResponseBookClub.id());
        api.users.deleteUserAuthorized(actualAccessToken);
        api.users.deleteUserAuthorized(actualAccessToken_1);

    }

}
