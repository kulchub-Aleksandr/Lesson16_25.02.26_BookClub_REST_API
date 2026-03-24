package tests.clubs;

import models.clubs.registrationBookClub.SuccessfulBookClubRegistrationBodyModel;
import models.clubs.registrationBookClub.SuccessfulBookClubRegistrationResponseModel;
import models.clubs.updateBookClub.SuccessfulBookClubUpdateBodyModel;
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

    private String bookTitle_1;
    private String bookAuthors_1;
    private Integer publicationYear_1;
    private String description_1;
    private String telegramChatLink_1;


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

        bookTitle_1 = testData.getBookTitle() + "  qa.guru_039 AlexKulch + 1";
        bookAuthors_1 = testData.getBookAuthor();
        publicationYear_1 = testData.getPublicationYear();
        description_1 = testData.getBookDescription();
        telegramChatLink_1 = testData.getTelegramChatLink();

    }


    @Test
    @DisplayName("Тест на вхождение в члены клуба, с авторизованным пользователем, с созданием клуба")
    public void membershipClubWithAnAuthorizedUserCreatingClubTest() {

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
            assertThat(members).hasSize(2)
                    .as("Клуб должен содержать ровно двух участников");

            assertThat(members.get(0)).isEqualTo(registrationUserResponse.id())
                    .as("Первый в списке участников должен быть владелец клуба");
            assertThat(members.get(1)).isEqualTo(registrationUserResponse_1.id())
                    .as("Второй в списке участников должен быть новый участник");

        });

        api.clubs.bookClubDelete(actualAccessToken, registrationResponseBookClub.id());
        api.users.deleteUserAuthorized(actualAccessToken);
        api.users.deleteUserAuthorized(actualAccessToken_1);

    }


}
