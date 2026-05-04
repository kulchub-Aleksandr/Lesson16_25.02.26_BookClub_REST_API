package tests.clubs;

import allure.Layer;
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

import java.util.List;

import static io.qameta.allure.Allure.step;
import static org.assertj.core.api.Assertions.assertThat;

@Layer("Club")
public class BookClubMembersTests extends TestBase {
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
    @DisplayName("Тест на вхождение в члены клуба, с авторизованным пользователем, с созданием клуба")
    public void membershipClubWithAnAuthorizedUserCreatingClubTest() {

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


        step("Отладка: проверка значений перед сравнением", () -> {
            System.out.println("response.members(): " + response.members());
            System.out.println("response.members().get(0): " + response.members().get(0)); // владелец
            System.out.println("response.members().get(1): " + response.members().get(1)); // второй участник
            System.out.println("registrationUserResponse.id(): " + registrationUserResponse.id()); // ID первого пользователя
            System.out.println("registrationUserResponse_1.id(): " + registrationUserResponse_1.id()); // ID второго пользователя
        });

        System.out.println("response.members(): " + response.members());
        System.out.println("response.members().get(1): " + response.members().get(1));
        System.out.println("registrationUserResponse.id(): " + registrationUserResponse.id());
        System.out.println("Сравнение: " + (response.members().get(1) == registrationUserResponse.id()));
        System.out.println("equals: " + response.members().get(1).equals(registrationUserResponse.id()));

        step("Проверка что в члены клуба добавился второй пользователь", () -> {
            assertThat(response.id()).isGreaterThan(0);
            assertThat(response.owner()).isGreaterThan(0);
            assertThat(response.members()).contains(response.owner());
            assertThat(response.members()).contains(registrationUserResponse_1.id());

            List<Integer> members = response.members();
            assertThat(members)
                    .as("Клуб должен содержать ровно двух участников")
                    .hasSize(2);
            assertThat(members.get(0))
                    .as("Первый в списке участников должен быть владелец клуба")
                    .isEqualTo(registrationUserResponse.id());
            assertThat(members.get(1))
                    .as("Второй в списке участников должен быть новый участник")
                    .isEqualTo(registrationUserResponse_1.id());
        });

        api.clubs.bookClubDelete(actualAccessToken, registrationResponseBookClub.id());
        api.users.deleteUserAuthorized(actualAccessToken);
        api.users.deleteUserAuthorized(actualAccessToken_1);
    }


    @Test
    @DisplayName("Тест на выход из членов клуба, с авторизованным пользователем, с созданием клуба")
    public void membershipDeleteClubWithAnAuthorizedUserCreatingClubTest() {

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

        api.clubs.bookClubMemberDelete(actualAccessToken_1, registrationResponseBookClub.id());

        SuccessfulBookClubRegistrationResponseModel response_2
                = api.clubs.getClubById(actualAccessToken, registrationResponseBookClub.id());

        step("Проверка что из членов клуба удалился второй пользователь", () -> {
            assertThat(response_2.id()).isGreaterThan(0);
            assertThat(response_2.owner()).isGreaterThan(0);
            assertThat(response_2.members()).contains(response_2.owner());
            List<Integer> members = response_2.members();
            assertThat(members)
                    .as("Клуб должен содержать одного участника")
                    .hasSize(1);
        });

        api.clubs.bookClubDelete(actualAccessToken, registrationResponseBookClub.id());
        api.users.deleteUserAuthorized(actualAccessToken);
        api.users.deleteUserAuthorized(actualAccessToken_1);
    }

}
