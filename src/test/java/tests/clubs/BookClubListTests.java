package tests.clubs;

import models.clubs.listBookClub.BookClubsListResponseModel;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import tests.TestBase;

import static io.qameta.allure.Allure.step;
import static org.assertj.core.api.Assertions.assertThat;

public class BookClubListTests extends TestBase {

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
    @DisplayName("Тест фильтрации списка клубов: поиск по названию и членству")
    public void getClubsListBookTitleMembershipTest() {

        BookClubsListResponseModel response = step("Тест на получение клуба по названию клуба \"Сети\" и membership=owner", () -> {
           String search = "Сети";
            String membership = "owner";
              return   api.clubs.getClubsBookClubsBookTitleMembershipList(search,1, 10, membership);
        });

        step("Проверка соответствия полученных данных в ответе", () -> {
            assertThat(response).isNotNull();
            assertThat(response.count()).isGreaterThanOrEqualTo(0);
            assertThat(response.results()).isNotNull();

        });
    }
    @Test
    @DisplayName("Тест фильтрации списка клубов: поиск по названию и членству")
    public void getClubsListMembershipTest() {

        BookClubsListResponseModel response = step("Тест на получение клуба по названию клуба \"Сети\" и membership=owner", () -> {

            String membership = "owner";
              return   api.clubs.getClubsBookClubsMembershipList(1, 10, membership);
        });

        step("Проверка соответствия полученных данных в ответе", () -> {
            assertThat(response).isNotNull();
            assertThat(response.count()).isGreaterThanOrEqualTo(0);
            assertThat(response.results()).isNotNull();

        });
    }
}
