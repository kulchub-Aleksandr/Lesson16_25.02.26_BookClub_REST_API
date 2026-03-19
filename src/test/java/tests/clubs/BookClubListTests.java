package tests.clubs;

import models.clubs.listBookClub.BookClubsListResponseModel;
import models.clubs.listBookClub.ClubModel;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import tests.TestBase;

import static io.qameta.allure.Allure.step;
import static org.assertj.core.api.Assertions.assertThat;

public class BookClubListTests extends TestBase {

    @Test
    @DisplayName("Тест на получение списка клубов")
    public void getClubsListReturns200AndValidStructure() {

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
    public void getClubsListBookTitle() {

        BookClubsListResponseModel response =
                step("Тест на получение клуба по названию клуба \"Сети\"", () -> {
                    String search = "Сети";
                    return api.clubs.getClubsListBookTitle(search, 1, 10);
                });

        step("Проверка соответствия полученных данных в ответе", () -> {
            assertThat(response).isNotNull();
            assertThat(response.count()).isGreaterThanOrEqualTo(0);
            assertThat(response.results()).isNotNull();
            assertThat(response.results()).hasSize(response.count());

            System.out.println("Количество книг: " + response.count());
            System.out.println("Есть результаты: " + !response.results().isEmpty());
            assertThat(response.results().getFirst().bookTitle()).isEqualTo("Сети");

        });

    }
}
