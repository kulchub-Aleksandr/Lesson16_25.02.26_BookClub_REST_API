package tests.clubs.UI;

import models.clubs.registrationBookClub.SuccessfulBookClubRegistrationBodyModel;
import models.clubs.registrationBookClub.SuccessfulBookClubRegistrationResponseModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import tests.TestBase;
import tests.TestData;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.*;

public class BookClubReviewsTests extends TestBase {

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
        // оставляем генерацию данных в тесте, чтобы каждый запуск был с новыми пользователями
        username = testData.getUsername();
        password = "12345";

        bookTitle = testData.getBookTitle() + "  [qa.guru_039 AlexKulch]";
        bookAuthors = testData.getBookAuthor();
        publicationYear = testData.getPublicationYear();
        description = testData.getBookDescription();
        telegramChatLink = testData.getTelegramChatLink();
    }

    @Test
    public void cantLeaveClubAsOwnerTest() {
        // register user
        models.users.registration.SuccessfulRegistrationResponseModel registrationResponse
                = api.users.registration(new models.users.registration.RegistrationBodyModel(username, password));

        // login user

        String actualAccessToken = api.auth.loginAndGetAccessToken(new models.users.login.LoginBodyModel(username, password));
        String actualRefreshToken = api.auth.loginAndGetRefreshToken(new models.users.login.LoginBodyModel(username, password));


//        LoginBodyModel loginData = new LoginBodyModel(username, password);
//        SuccessfulLoginResponseModel loginResponse = api.auth.login(loginData);
//
//        String accessToken = loginResponse.access();
//        String refreshToken = loginResponse.refresh();

        // todo move to model
        String localStorageAuthBody = """
                {
                  "user": {
                    "id": %d,
                    "username": "%s",
                    "firstName": "%s",
                    "lastName": "%s",
                    "email": "%s",
                    "remoteAddr": "%s"
                  },
                  "accessToken": "%s",
                  "refreshToken": "%s",
                  "isAuthenticated": true
                }
                """.formatted(
                registrationResponse.id(),
                registrationResponse.username(),
                registrationResponse.firstName(),
                registrationResponse.lastName(),
                registrationResponse.email(),
                registrationResponse.remoteAddr(),
                actualAccessToken,
                actualRefreshToken
        );

        // create club
        SuccessfulBookClubRegistrationResponseModel registrationBookClubResponse
                = api.clubs.bookClubsRegistration(actualAccessToken, new SuccessfulBookClubRegistrationBodyModel(
                bookTitle,
                bookAuthors,
                publicationYear,
                description,
                telegramChatLink));

        String clubId = registrationBookClubResponse.id().toString();

        // open club
        open("/favicon.ico");
        localStorage().setItem("book_club_auth", localStorageAuthBody);
        open("/clubs/" + clubId);

        // cant leave club as owner
        $(".club-content").shouldBe(visible);
        $(".leave-btn").click();
        confirm();
        $(".error").shouldHave(text("Не удалось покинуть клуб"));


        api.clubs.bookClubDelete(actualAccessToken, registrationBookClubResponse.id());
        api.users.deleteUserAuthorized(actualAccessToken);
    }

    @Test
//    @WithNewUser
//    @WithNewClub
    public void cantLeaveClubAsOwnerTest_with_extensions() {
        // cant leave club as owner
        $(".club-content").shouldBe(visible);
        $(".leave-btn").click();
        confirm();
        $(".error").shouldHave(text("Не удалось покинуть клуб"));
    }

//


    @Test
    @Disabled
    public void cantLeaveClubAsAdminTest_with_login_by_api() {
        // register user
        RegistrationBodyModel registrationData = new RegistrationBodyModel(username, password);
        SuccessfulRegistrationResponseModel registrationResponse = api.users.register(registrationData);
        // login user
        LoginBodyModel loginData = new LoginBodyModel(username, password);
        SuccessfulLoginResponseModel loginResponse = api.auth.login(loginData);

        String accessToken = loginResponse.access();
        String refreshToken = loginResponse.refresh();

        String localStorageAuthBody = """
                {
                  "user": {
                    "id": %d,
                    "username": "%s",
                    "firstName": "%s",
                    "lastName": "%s",
                    "email": "%s",
                    "remoteAddr": "%s"
                  },
                  "accessToken": "%s",
                  "refreshToken": "%s",
                  "isAuthenticated": true
                }
                """.formatted(
                registrationResponse.id(),
                registrationResponse.username(),
                registrationResponse.firstName(),
                registrationResponse.lastName(),
                registrationResponse.email(),
                registrationResponse.remoteAddr(),
                accessToken,
                refreshToken
        );

        open("/favicon.ico");
        localStorage().setItem("book_club_auth", localStorageAuthBody);
        open("/");

        // create club
        // todo create test for navigation from Main page to Create club page
        open("https://book-club.qa.guru/clubs/create");
        $("[data-testid=create-club-link]").click();
        $("#bookTitle").setValue(username);
        $("#bookAuthors").setValue(username);
        $("#publicationYear").setValue("2020");
        $("#description").setValue(username);
        $("#telegramChatLink").setValue("https://t.me/qa_guru" + username).pressEnter();

        // open club
        $(".clubs-list").$(byText(username))
                .parent().parent().$(".open-btn").click();

        $(".clubs-list").$(byText(username))
                .parent().parent().$(".open-btn").click();

        // wrong leave club
        $(".club-content").shouldBe(visible);
        $(".leave-btn").click();
        confirm();
        $(".error").shouldHave(text("Не удалось покинуть клуб"));
    }

    @Test
    @Disabled
    public void cantLeaveClubAsAdminTest_without_api() {
        // register user
        open("https://book-club.qa.guru/signup");
        $("[data-testid=username-input]").setValue(username);
        $("[data-testid=password-input]").setValue(password);
        $("[data-testid=confirm-password-input]").setValue(password).pressEnter();
        $("[data-testid=signup-button]").should(disappear);

//        // login user
//        $("[data-testid=username-input]").setValue(username);
//        $("[data-testid=password-input]").setValue(password).pressEnter();
//        $("[data-testid=signin-button]").should(disappear);

        // create club
        // todo create test for navigation from Main page to Create club page
        // open("https://book-club.qa.guru/clubs/create");
        $("[data-testid=create-club-link]").click();
        $("#bookTitle").setValue(username);
        $("#bookAuthors").setValue(username);
        $("#publicationYear").setValue("2020");
        $("#description").setValue(username);
        $("#telegramChatLink").setValue("https://t.me/qa_guru" + username).pressEnter();

        // open club
        $(".clubs-list").$(byText(username))
                .parent().parent().$(".open-btn").click();

        // wrong leave club
        $(".club-content").shouldBe(visible);
        $(".leave-btn").click();
        confirm();
        $(".error").shouldHave(text("Не удалось покинуть клуб"));
    }
}
