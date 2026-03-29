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

        SuccessfulRegistrationResponseModel registrationUserResponse_1
                = api.users.registration(new RegistrationBodyModel(username_1, password_1));

        step("Проверка соответствия отправленных данных с данными в ответе", () -> {
            assertThat(registrationUserResponse_1.username()).isEqualTo(username_1);
        });

        String actualAccessToken_1 = api.auth.loginAndGetAccessToken(new LoginBodyModel(username_1, password_1));

        api.clubs.bookClubMemberRegistration(actualAccessToken_1, registrationResponseBookClub.id());

        SuccessfulBookClubRegistrationResponseModel response
                = api.clubs.getClubById(actualAccessToken_1, registrationResponseBookClub.id());

        step("Проверка что в члены клуба добавился второй пользователь", () -> {
            assertThat(response.members()).contains(response.owner());
            assertThat(response.members()).contains(registrationUserResponse_1.id());
        });

        SuccessfulReviewsPostBookClubResponseModel reviewsResponse
                = api.clubs.bookClubReviewsPost(actualAccessToken_1,
                new SuccessfulReviewsPostBookClubBodyModel(
                        response.id(),
                        newReview,
                        5,
                        22));

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

        SuccessfulRegistrationResponseModel registrationUserResponse
                = api.users.registration(new RegistrationBodyModel(username, password));

        step("Проверка соответствия отправленных данных с данными в ответе", () -> {
            assertThat(registrationUserResponse.username()).isEqualTo(username);
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

        SuccessfulRegistrationResponseModel registrationUserResponse_1
                = api.users.registration(new RegistrationBodyModel(username_1, password_1));

        step("Проверка соответствия отправленных данных с данными в ответе", () -> {
            assertThat(registrationUserResponse_1.username()).isEqualTo(username_1);
        });

        String actualAccessToken_1 = api.auth.loginAndGetAccessToken(new LoginBodyModel(username_1, password_1));

        api.clubs.bookClubMemberRegistration(actualAccessToken_1, registrationResponseBookClub.id());

        SuccessfulBookClubRegistrationResponseModel response
                = api.clubs.getClubById(actualAccessToken_1, registrationResponseBookClub.id());

        step("Проверка что в члены клуба добавился второй пользователь", () -> {
            assertThat(response.members()).contains(response.owner());
            assertThat(response.members()).contains(registrationUserResponse_1.id());
        });

        SuccessfulReviewsPostBookClubResponseModel reviewsResponse
                = api.clubs.bookClubReviewsPost(actualAccessToken_1,
                new SuccessfulReviewsPostBookClubBodyModel(
                        response.id(),
                        newReview,
                        5,
                        22));

        step("Проверка что отзыв второго пользователя добавился ", () -> {
            assertThat(reviewsResponse.id()).isGreaterThan(0);
            assertThat(reviewsResponse.club()).isGreaterThan(0);
            assertThat(reviewsResponse.user().id()).isEqualTo(registrationUserResponse_1.id());
            assertThat(reviewsResponse.user().username()).isEqualTo(registrationUserResponse_1.username());
            assertThat(reviewsResponse.review()).isEqualTo(newReview);
        });

        SuccessfulReviewsGetBookClubResponseModel reviewsListResponse
                = api.clubs.getReviewsBookClub(registrationResponseBookClub.id(), 1, 100);

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

    @Test
    @DisplayName("Тест на вызов отзывов на книгу, с авторизованным пользователем, не членом клуба, с созданием клуба")
    public void getReviewsBookClubWithAnAuthorizedUserNotMemberClubCreatingClubTest() {

        SuccessfulRegistrationResponseModel registrationUserResponse
                = api.users.registration(new RegistrationBodyModel(username, password));

        step("Проверка соответствия отправленных данных с данными в ответе", () -> {
            assertThat(registrationUserResponse.username()).isEqualTo(username);
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

        SuccessfulRegistrationResponseModel registrationUserResponse_1
                = api.users.registration(new RegistrationBodyModel(username_1, password_1));

        step("Проверка соответствия отправленных данных с данными в ответе", () -> {
            assertThat(registrationUserResponse_1.username()).isEqualTo(username_1);
        });

        String actualAccessToken_1 = api.auth.loginAndGetAccessToken(new LoginBodyModel(username_1, password_1));

//        api.clubs.bookClubMemberRegistration(actualAccessToken_1, registrationResponseBookClub.id());

//        SuccessfulBookClubRegistrationResponseModel response
//                = api.clubs.getClubById(actualAccessToken_1, registrationResponseBookClub.id());
//
//        step("Проверка что в члены клуба добавился второй пользователь", () -> {
//            assertThat(response.members()).contains(response.owner());
//            assertThat(response.members()).contains(registrationUserResponse_1.id());
//        });

        SuccessfulReviewsPostBookClubResponseModel reviewsResponse
                = api.clubs.bookClubReviewsPost(actualAccessToken_1,
                new SuccessfulReviewsPostBookClubBodyModel(
                        registrationResponseBookClub.id(),
                        newReview,
                        5,
                        22));

        step("Проверка что отзыв второго пользователя добавился ", () -> {
            assertThat(reviewsResponse.id()).isGreaterThan(0);
            assertThat(reviewsResponse.club()).isGreaterThan(0);
            assertThat(reviewsResponse.user().id()).isEqualTo(registrationUserResponse_1.id());
            assertThat(reviewsResponse.user().username()).isEqualTo(registrationUserResponse_1.username());
            assertThat(reviewsResponse.review()).isEqualTo(newReview);
        });

        SuccessfulReviewsGetBookClubResponseModel reviewsListResponse
                = api.clubs.getReviewsBookClub(registrationResponseBookClub.id(), 1, 100);

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

    @Test
    @DisplayName("Тест на удаление отзыва на книгу, с авторизованным пользователем, с созданием клуба")
    public void deleteReviewsBookClubWithAnAuthorizedUserCreatingClubTest() {

        SuccessfulRegistrationResponseModel registrationUserResponse
                = api.users.registration(new RegistrationBodyModel(username, password));

        step("Проверка соответствия отправленных данных с данными в ответе", () -> {
            assertThat(registrationUserResponse.username()).isEqualTo(username);
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

        SuccessfulRegistrationResponseModel registrationUserResponse_1
                = api.users.registration(new RegistrationBodyModel(username_1, password_1));

        step("Проверка соответствия отправленных данных с данными в ответе", () -> {
            assertThat(registrationUserResponse_1.username()).isEqualTo(username_1);
        });

        String actualAccessToken_1 = api.auth.loginAndGetAccessToken(new LoginBodyModel(username_1, password_1));


        step("Регистрация нового члена клуба", () -> {
            api.clubs.bookClubMemberRegistration(actualAccessToken_1, registrationResponseBookClub.id());
        });

        SuccessfulBookClubRegistrationResponseModel response =
                step("Тест на получение информации клуба по ID и проверка что данные изменились", () -> {

                    return api.clubs.getClubById(actualAccessToken_1, registrationResponseBookClub.id());
                });

        step("Проверка что в члены клуба добавился второй пользователь", () -> {
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
            assertThat(reviewsResponse.user().id()).isEqualTo(registrationUserResponse_1.id());
            assertThat(reviewsResponse.user().username()).isEqualTo(registrationUserResponse_1.username());
        });

        SuccessfulReviewsGetBookClubResponseModel reviewsListResponse
                = api.clubs.getReviewsBookClub(registrationResponseBookClub.id(), 1, 100);

        step("Проверка что отзывы видны ", () -> {
            assertThat(reviewsListResponse.count()).isGreaterThan(0);
            assertThat(reviewsListResponse.results().getFirst().user().id()).isEqualTo(registrationUserResponse_1.id());
        });

        api.clubs.bookClubReviewsDelete(actualAccessToken_1, reviewsResponse.id());

        SuccessfulReviewsGetBookClubResponseModel reviewsListResponse_1
                = api.clubs.getReviewsBookClub(registrationResponseBookClub.id(), 1, 100);

        step("Проверка что отзыв удалился", () -> {
            assertThat(reviewsListResponse_1.count()).isEqualTo(0);
            assertThat(reviewsListResponse_1.results().isEmpty()).isTrue();
        });

        api.clubs.bookClubDelete(actualAccessToken, registrationResponseBookClub.id());
        api.users.deleteUserAuthorized(actualAccessToken);
        api.users.deleteUserAuthorized(actualAccessToken_1);
    }
}
