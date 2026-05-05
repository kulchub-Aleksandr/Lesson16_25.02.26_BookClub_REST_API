package tests.clubs.ui_tests;

import allure.Layer;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.qameta.allure.Story;
import models.clubs.localStorage.LocalStorageAuthRequestBody;
import models.clubs.localStorage.UserData;
import models.clubs.registrationBookClub.SuccessfulBookClubRegistrationBodyModel;
import models.clubs.registrationBookClub.SuccessfulBookClubRegistrationResponseModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import tests.TestBase;
import tests.TestData;

import java.time.Duration;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.*;

public class BookClubOwnerLeaveUITests extends TestBase {

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
    @Story("Выход из членства клуба")
    @Tag("API+UI")
    @DisplayName("Пользователь не может покинуть клуб, если он его владелец")
    public void cantLeaveClubAsOwnerTest() {
        // register user
        models.users.registration.SuccessfulRegistrationResponseModel registrationResponse
                = api.users.registration(new models.users.registration.RegistrationBodyModel(username, password));

        // login user

        String actualAccessToken = api.auth.loginAndGetAccessToken(new models.users.login.LoginBodyModel(username, password));
        String actualRefreshToken = api.auth.loginAndGetRefreshToken(new models.users.login.LoginBodyModel(username, password));

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
        localStorage().setItem("book_club_auth", localStorageAuthJson);
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
    @Story("Выход из членства клуба")
    @Tag("API+UI")
    @DisplayName("UI + API Пользователь не может покинуть клуб, если он его владелец, вариант без LocalStorageAuthRequestBody")
    //@Disabled
    public void cantLeaveClubAsAdminTest_with_login_by_api() {
        // register user
        models.users.registration.SuccessfulRegistrationResponseModel registrationResponse
                = api.users.registration(new models.users.registration.RegistrationBodyModel(username, password));

        // login user
        String actualAccessToken = api.auth.loginAndGetAccessToken(new models.users.login.LoginBodyModel(username, password));
        String actualRefreshToken = api.auth.loginAndGetRefreshToken(new models.users.login.LoginBodyModel(username, password));

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

        open("/favicon.ico");
        localStorage().setItem("book_club_auth", localStorageAuthBody);
        open("/");

        // create club
        // todo create test for navigation from Main page to Create club page
        open("/clubs/create");
        $("[data-testid=create-club-link]").click();
        $("#bookTitle").setValue(username);
        $("#bookAuthors").setValue(username);
        $("#publicationYear").setValue("2020");
        $("#description").setValue(username);
        $("#telegramChatLink").setValue("https://t.me/qa_guru" + username).pressEnter();

        // open club

        ElementsCollection paginationButtons = $(".pagination-buttons").shouldBe(visible).$$("button.pagination-button");

        SelenideElement targetButton = null;
        for (int i = 0; i < paginationButtons.size(); i++) {
            SelenideElement currentButton = paginationButtons.get(i);
            if (currentButton.getText().trim().equals("Вперед")) {
                if (i > 0) {
                    targetButton = paginationButtons.get(i - 1);
                    break;
                }
            }
        }

        if (targetButton != null) {
            targetButton
                    .shouldBe(visible, Duration.ofSeconds(5))
                    .should(matchText("\\d+"))
                    .click();
            System.out.println("Кликаем на страницу: " + targetButton.getText());
        } else {
            throw new RuntimeException("Кнопка перед 'Вперед' не найдена");
        }

        $(".clubs-list").$(byText(username))
                .parent().parent().$(".open-btn").click();


        // wrong leave club
        $(".club-content").shouldBe(visible);
        $(".leave-btn").click();
        confirm();
        $(".error").shouldHave(text("Не удалось покинуть клуб"));

    }


    @Test
    @Story("Выход из членства клуба")
    @Tag("UI")
    @DisplayName("Пользователь не может покинуть клуб, если он его владелец")
    //@Disabled
    public void cantLeaveClubAsAdminTest_without_api() {
        // register user
        open("/signup");
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


        ElementsCollection paginationButtons = $(".pagination-buttons").shouldBe(visible).$$("button.pagination-button");

        SelenideElement targetButton = null;
        for (int i = 0; i < paginationButtons.size(); i++) {
            SelenideElement currentButton = paginationButtons.get(i);
            if (currentButton.getText().trim().equals("Вперед")) {
                if (i > 0) {
                    targetButton = paginationButtons.get(i - 1);
                    break;
                }
            }
        }

        if (targetButton != null) {
            targetButton
                    .shouldBe(visible, Duration.ofSeconds(5))
                    .should(matchText("\\d+"))
                    .click();
            System.out.println("Кликаем на страницу: " + targetButton.getText());
        } else {
            throw new RuntimeException("Кнопка перед 'Вперед' не найдена");
        }

        $(".clubs-list").$(byText(username))
                .parent().parent().$(".open-btn").click();

        // wrong leave club
        $(".club-content").shouldBe(visible);
        $(".leave-btn").click();
        confirm();
        $(".error").shouldHave(text("Не удалось покинуть клуб"));
    }
}
