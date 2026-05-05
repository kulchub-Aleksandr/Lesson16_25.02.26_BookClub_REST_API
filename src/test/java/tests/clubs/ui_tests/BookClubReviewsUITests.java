package tests.clubs.ui_tests;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.qameta.allure.Story;
import models.clubs.localStorage.LocalStorageAuthRequestBody;
import models.clubs.localStorage.UserData;
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
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import pages.ClubPage;
import tests.TestBase;
import tests.TestData;

import static io.qameta.allure.Allure.step;
import static org.assertj.core.api.Assertions.assertThat;

public class BookClubReviewsUITests extends TestBase {

    ClubPage clubPage = new ClubPage();
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


    private int assessment;
    private int newAssessment;
    private int readPages;
    private int newReadPages;

    private String newReview;
    private String editedReview;


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

        newAssessment = testData.newAssessment;
        assessment = testData.assessment;
        newReadPages = testData.newReadPages;
        readPages = testData.readPages;

        newReview = "Пробный отзыв";
        editedReview = newReview + " Редактирование отзыва";
    }

    @Test
    @Story("Оставление отзыва на книгу")
    @Tag("API+UI")
    @DisplayName("Тест на оставление отзыва на книгу, с авторизованным пользователем, не создателем клуба," +
            "с созданием нового клуба и новых пользователей")
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

        String clubId = registrationResponseBookClub.id().toString();

        SuccessfulRegistrationResponseModel registrationUserResponse_1
                = api.users.registration(new RegistrationBodyModel(username_1, password_1));

        step("Проверка соответствия отправленных данных с данными в ответе", () -> {
            assertThat(registrationUserResponse_1.username()).isEqualTo(username_1);
        });

        String actualAccessToken_1 = api.auth.loginAndGetAccessToken(new LoginBodyModel(username_1, password_1));
        String actualRefreshToken_1 = api.auth.loginAndGetRefreshToken(new LoginBodyModel(username_1, password_1));

        api.clubs.bookClubMemberRegistration(actualAccessToken_1, registrationResponseBookClub.id());

        SuccessfulBookClubRegistrationResponseModel getClubByIdResponse
                = api.clubs.getClubById(actualAccessToken_1, registrationResponseBookClub.id());

        step("Проверка что в члены клуба добавился второй пользователь", () -> {
            assertThat(getClubByIdResponse.members()).contains(getClubByIdResponse.owner());
            assertThat(getClubByIdResponse.members()).contains(registrationUserResponse_1.id());
        });

        UserData userData = new UserData(
                registrationResponse.id(),
                registrationResponse.username(),
                registrationResponse.firstName(),
                registrationResponse.lastName(),
                registrationResponse.email(),
                registrationResponse.remoteAddr());

        LocalStorageAuthRequestBody localStorageAuthBody = new LocalStorageAuthRequestBody(
                userData,
                actualAccessToken_1,
                actualRefreshToken_1,
                true);

        ObjectMapper objectMapper = new ObjectMapper();
        String localStorageAuthJson;
        try {
            localStorageAuthJson = objectMapper.writeValueAsString(localStorageAuthBody);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize LocalStorageAuthRequestBody to JSON", e);
        }

        clubPage.openPage(localStorageAuthJson)
                .openClubPage(registrationResponseBookClub.id())
                .clubContentCheck()
                .pressReviewButton()
                .setAssessment(assessment)
                .setReadPages(readPages)
                .setReview(newReview)
                .saveReviewButton()
                .checkResult(clubPage.getReviewerName(), username_1)
                .checkResult(clubPage.getReview(), newReview)
                .checkResult(clubPage.getReadPages(), String.valueOf(readPages));

        api.clubs.bookClubDelete(actualAccessToken, registrationResponseBookClub.id());
        api.users.deleteUserAuthorized(actualAccessToken);
        api.users.deleteUserAuthorized(actualAccessToken_1);

    }

    @Test
    @Story("Оставление отзыва на книгу")
    @Tag("API+UI")
    @DisplayName("Тест на оставление отзыва на книгу, с авторизованным пользователем,  Создателем клуба," +
            "с созданием нового клуба и новых пользователей")
    public void bookClubReviewsPostWithAnAuthorizedUserClubOwnerCreatingClubTest() {

        SuccessfulRegistrationResponseModel registrationResponse
                = api.users.registration(new RegistrationBodyModel(username, password));

        step("Проверка соответствия отправленных данных с данными в ответе", () -> {
            assertThat(registrationResponse.username()).isEqualTo(username);
        });

        String actualAccessToken = api.auth.loginAndGetAccessToken(new LoginBodyModel(username, password));
        String actualRefreshToken = api.auth.loginAndGetRefreshToken(new LoginBodyModel(username, password));

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
        String clubId = registrationResponseBookClub.id().toString();

        UserData userData = new UserData(
                registrationResponse.id(),
                registrationResponse.username(),
                registrationResponse.firstName(),
                registrationResponse.lastName(),
                registrationResponse.email(),
                registrationResponse.remoteAddr());

        LocalStorageAuthRequestBody localStorageAuthBody = new LocalStorageAuthRequestBody(
                userData,
                actualAccessToken,
                actualRefreshToken,
                true);

        ObjectMapper objectMapper = new ObjectMapper();
        String localStorageAuthJson;
        try {
            localStorageAuthJson = objectMapper.writeValueAsString(localStorageAuthBody);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize LocalStorageAuthRequestBody to JSON", e);
        }

        clubPage.openPage(localStorageAuthJson)
                .openClubPage(registrationResponseBookClub.id())
                .clubContentCheck()
                .pressReviewButton()
                .setAssessment(assessment)
                .setReadPages(readPages)
                .setReview(newReview)
                .saveReviewButton()
                .checkResult(clubPage.getReviewerName(), username)
                .checkResult(clubPage.getReview(), newReview)
                .checkResult(clubPage.getReadPages(), String.valueOf(readPages));

        api.clubs.bookClubDelete(actualAccessToken, registrationResponseBookClub.id());
        api.users.deleteUserAuthorized(actualAccessToken);

    }

    @Test
    @Story("Оставление отзыва на книгу")
    @Tag("API+UI")
    @DisplayName("Тест на редактирование отзыва на книгу, с авторизованным пользователем, не создателем клуба, " +
            "с созданием нового клуба и новых пользователей")
    public void bookClubReviewsPatchWithAnAuthorizedUserCreatingClubTest() {

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
        String actualRefreshToken_1 = api.auth.loginAndGetRefreshToken(new LoginBodyModel(username_1, password_1));


        api.clubs.bookClubMemberRegistration(actualAccessToken_1, registrationResponseBookClub.id());

        SuccessfulBookClubRegistrationResponseModel getClubByIdResponse
                = api.clubs.getClubById(actualAccessToken_1, registrationResponseBookClub.id());

        step("Проверка что в члены клуба добавился второй пользователь", () -> {
            assertThat(getClubByIdResponse.members()).contains(getClubByIdResponse.owner());
            assertThat(getClubByIdResponse.members()).contains(registrationUserResponse_1.id());
        });

        SuccessfulReviewsPostBookClubResponseModel reviewsResponse
                = api.clubs.bookClubReviewsPost(actualAccessToken_1,
                new SuccessfulReviewsPostBookClubBodyModel(
                        getClubByIdResponse.id(),
                        newReview,
                        assessment,
                        readPages));

        step("Проверка что отзыв второго пользователя добавился ", () -> {
            assertThat(reviewsResponse.id()).isGreaterThan(0);
            assertThat(reviewsResponse.club()).isGreaterThan(0);
            assertThat(reviewsResponse.user().id()).isEqualTo(registrationUserResponse_1.id());
            assertThat(reviewsResponse.user().username()).isEqualTo(registrationUserResponse_1.username());
            assertThat(reviewsResponse.review()).isEqualTo(newReview);
        });

        UserData userData = new UserData(
                registrationUserResponse_1.id(),
                registrationUserResponse_1.username(),
                registrationUserResponse_1.firstName(),
                registrationUserResponse_1.lastName(),
                registrationUserResponse_1.email(),
                registrationUserResponse_1.remoteAddr());

        LocalStorageAuthRequestBody localStorageAuthBody = new LocalStorageAuthRequestBody(
                userData,
                actualAccessToken_1,
                actualRefreshToken_1,
                true);

        ObjectMapper objectMapper = new ObjectMapper();
        String localStorageAuthJson;
        try {
            localStorageAuthJson = objectMapper.writeValueAsString(localStorageAuthBody);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize LocalStorageAuthRequestBody to JSON", e);
        }

        clubPage.openPage(localStorageAuthJson)
                .openClubPage(registrationResponseBookClub.id())
                .clubContentCheck()
                .pressEditReviewButton()
                .setAssessment(newAssessment)
                .setReadPages(newReadPages)
                .setReview(editedReview)
                .saveReviewButton()
                .checkResult(clubPage.getReviewerName(), username_1)
                .checkResult(clubPage.getReview(), editedReview)
                .checkResult(clubPage.getReadPages(), String.valueOf(newReadPages));

        api.clubs.bookClubDelete(actualAccessToken, registrationResponseBookClub.id());
        api.users.deleteUserAuthorized(actualAccessToken);
        api.users.deleteUserAuthorized(actualAccessToken_1);

    }

    @Test
    @Story("Оставление отзыва на книгу")
    @Tag("API+UI")
    @DisplayName("Тест на удаление отзыва на книгу, с авторизованным пользователем," +
            " с созданием нового клуба и новых пользователей")
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

        String clubId = registrationResponseBookClub.id().toString();

        SuccessfulRegistrationResponseModel registrationUserResponse_1
                = api.users.registration(new RegistrationBodyModel(username_1, password_1));

        step("Проверка соответствия отправленных данных с данными в ответе", () -> {
            assertThat(registrationUserResponse_1.username()).isEqualTo(username_1);
        });

        String actualAccessToken_1 = api.auth.loginAndGetAccessToken(new LoginBodyModel(username_1, password_1));
        String actualRefreshToken_1 = api.auth.loginAndGetRefreshToken(new LoginBodyModel(username_1, password_1));


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
                            new SuccessfulReviewsPostBookClubBodyModel(
                                    response.id(),
                                    newReview,
                                    assessment,
                                    readPages);
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

        UserData userData = new UserData(
                registrationUserResponse.id(),
                registrationUserResponse.username(),
                registrationUserResponse.firstName(),
                registrationUserResponse.lastName(),
                registrationUserResponse.email(),
                registrationUserResponse.remoteAddr());

        LocalStorageAuthRequestBody localStorageAuthBody = new LocalStorageAuthRequestBody(
                userData,
                actualAccessToken_1,
                actualRefreshToken_1,
                true);

        ObjectMapper objectMapper = new ObjectMapper();
        String localStorageAuthJson;
        try {
            localStorageAuthJson = objectMapper.writeValueAsString(localStorageAuthBody);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize LocalStorageAuthRequestBody to JSON", e);
        }

        clubPage.openPage(localStorageAuthJson)
                .openClubPage(registrationResponseBookClub.id())
                .clubContentCheck()
                .deleteReview()
                .deleteCheckReview();

        api.clubs.bookClubDelete(actualAccessToken, registrationResponseBookClub.id());
        api.users.deleteUserAuthorized(actualAccessToken);
        api.users.deleteUserAuthorized(actualAccessToken_1);
    }
}
