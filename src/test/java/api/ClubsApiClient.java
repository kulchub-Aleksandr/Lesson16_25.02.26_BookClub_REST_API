package api;

import io.qameta.allure.Step;
import models.clubs.deleteBookClub.PermissionUnsuccessfulBookClubDeleteResponseModel;
import models.clubs.listBookClub.BookClubsListResponseModel;
import models.clubs.registrationBookClub.SuccessfulBookClubRegistrationBodyModel;
import models.clubs.registrationBookClub.SuccessfulBookClubRegistrationResponseModel;
import models.clubs.reviewsBookClub.SuccessfulReviewsGetBookClubResponseModel;
import models.clubs.reviewsBookClub.SuccessfulReviewsPostBookClubBodyModel;
import models.clubs.reviewsBookClub.SuccessfulReviewsPostBookClubResponseModel;
import models.clubs.updateBookClub.SuccessfulBookClubUpdateBodyModel;


import static io.restassured.RestAssured.given;
import static specs.clubs.bookClubDelete.BookClubDeleteSpec.permissionBookClubDeleteRequestSpec;
import static specs.clubs.bookClubDelete.BookClubDeleteSpec.permissionUnsuccessfulBookClubDeleteResponseSpec;
import static specs.clubs.bookClubList.BookClubListSpec.*;
import static specs.clubs.bookClubMembers.BookClubMembersSpec.*;
import static specs.clubs.bookClubRegistration.BookClubRegistrationSpec.bookClubRegistrationRequestSpec;
import static specs.clubs.bookClubRegistration.BookClubRegistrationSpec.successfulBookClubRegistrationResponseSpec;
import static specs.clubs.bookClubReviewsPost.BookClubReviewsSpec.*;
import static specs.clubs.bookClubUpdate.BookClubUpdateSpec.bookClubUpdateRequestSpec;
import static specs.clubs.bookClubUpdate.BookClubUpdateSpec.successfulBookClubUpdateResponseSpec;


public class ClubsApiClient {

    @Step("Регистрация клуба")
    public SuccessfulBookClubRegistrationResponseModel bookClubsRegistration(
            String accessToken,
            SuccessfulBookClubRegistrationBodyModel body) {
        return given(bookClubRegistrationRequestSpec)
                .header("Authorization", "Bearer " + accessToken)
                .body(body)
                .when()
                .post("/clubs/")
                .then()
                .spec(successfulBookClubRegistrationResponseSpec)
                .extract()
                .as(SuccessfulBookClubRegistrationResponseModel.class);
    }

    @Step("Обновление данных клуба методом PUT")
    public SuccessfulBookClubRegistrationResponseModel bookClubsUpdate(
            String accessToken,
            SuccessfulBookClubRegistrationBodyModel body,
            int id) {
        return given(bookClubUpdateRequestSpec)
                .header("Authorization", "Bearer " + accessToken)
                .body(body)
                .pathParam("id", id)
                .when()
                .put("/clubs/{id}/")
                .then()
                .spec(successfulBookClubUpdateResponseSpec)
                .extract()
                .as(SuccessfulBookClubRegistrationResponseModel.class);
    }

    @Step("Обновление данных клуба методом PATCH")
    public SuccessfulBookClubRegistrationResponseModel bookClubsPartialUpdate(
            String accessToken,
            SuccessfulBookClubUpdateBodyModel body,
            int id) {
        return given(bookClubUpdateRequestSpec)
                .header("Authorization", "Bearer " + accessToken)
                .body(body)
                .pathParam("id", id)
                .when()
                .patch("/clubs/{id}/")
                .then()
                .spec(successfulBookClubUpdateResponseSpec)
                .extract()
                .as(SuccessfulBookClubRegistrationResponseModel.class);
    }

    @Step("Удаление клуба")
    public void bookClubDelete(
            String accessToken, int id) {
        given(permissionBookClubDeleteRequestSpec)
                .header("Authorization", "Bearer " + accessToken)
                .pathParam("id", id)
                .when()
                .delete("/clubs/{id}/")
                .then()
                .log().all();
    }

    @Step("Удаление клуба с аккаунта не создателя клуба")
    public PermissionUnsuccessfulBookClubDeleteResponseModel bookClubPermissionDelete(
            String accessToken, int id) {
        return given(permissionBookClubDeleteRequestSpec)
                .header("Authorization", "Bearer " + accessToken)
                .pathParam("id", id)
                .when()
                .delete("/clubs/{id}/")
                .then()
                .log().all()
                .spec(permissionUnsuccessfulBookClubDeleteResponseSpec)
                .extract()
                .as(PermissionUnsuccessfulBookClubDeleteResponseModel.class);
    }

    @Step("Получение списка клубов GET /clubs/")
    public BookClubsListResponseModel getClubsList() {
        return given(clubsRequestSpec)
                .when()
                .get("/clubs/")
                .then()
                .spec(successfulBookClubsListResponseSpec)
                .extract()
                .as(BookClubsListResponseModel.class);
    }

    @Step("Получение клуба  GET /clubs/{id}")
    public SuccessfulBookClubRegistrationResponseModel getClubById(String accessToken, long id) {
        return given(clubsRequestSpec)
                .header("Authorization", "Bearer " + accessToken)
                .pathParam("id", id)
                .when()
                .get("/clubs/{id}/")
                .then()
                .spec(successfulBookClubListGetResponseSpec)
                .extract()
                .as(SuccessfulBookClubRegistrationResponseModel.class);
    }


    @Step("Получение клуба  GET /clubs/{id} после внесенных изменений")
    public SuccessfulBookClubRegistrationResponseModel getClubByIdAfterPut(String accessToken, long id) {
        return given(clubsRequestSpec)
                .header("Authorization", "Bearer " + accessToken)
                .pathParam("id", id)
                .when()
                .get("/clubs/{id}/")
                .then()
                .spec(successfulBookClubListGetAfterChangeResponseSpec)
                .extract()
                .as(SuccessfulBookClubRegistrationResponseModel.class);
    }


    @Step("Получение карточки клуба по названию клуба")
    public BookClubsListResponseModel getClubsListBookTitle(String search, int page, int page_size) {
        return given(clubsRequestSpec)
                .pathParam("search", search)
                .pathParam("page", page)
                .pathParam("page_size", page_size)
                .when()
                .get("/clubs/?search={search}&page={page}&page_size={page_size}")
                .then()
                .spec(successfulBookClubsListResponseSpec)
                .extract()
                .as(BookClubsListResponseModel.class);
    }

    @Step("Получение карточки клуба по названию и членству")
    public BookClubsListResponseModel getClubsBookClubsBookTitleMembershipList(String search, int page, int page_size, String membership) {
        return given(clubsRequestSpec)
                .pathParam("search", search)
                .pathParam("page", page)
                .pathParam("page_size", page_size)
                .pathParam("membership", membership)
                .when()
                .get("/clubs/?search={search}&page={page}&page_size={page_size}&membership={membership}")
                .then()
                .spec(successfulBookClubsListResponseSpec)
                .extract()
                .as(BookClubsListResponseModel.class);
    }

    @Step("Получение карточки клуба по членству в клубе")
    public BookClubsListResponseModel getClubsBookClubsMembershipList(int page, int page_size, String membership) {
        return given(clubsRequestSpec)
                .pathParam("page", page)
                .pathParam("page_size", page_size)
                .pathParam("membership", membership)
                .when()
                .get("/clubs/?page={page}&page_size={page_size}&membership={membership}")
                .then()
                .spec(successfulBookClubsListResponseSpec)
                .extract()
                .as(BookClubsListResponseModel.class);
    }

    @Step("Получение карточки клуба по членству в клубе")
    public BookClubsListResponseModel getClubsBookClubsMembershipListOwner(
            String accessToken,
            int page,
            int page_size,
            String membership) {
        return given(clubsRequestSpec)
                .header("Authorization", "Bearer " + accessToken)
                .pathParam("page", page)
                .pathParam("page_size", page_size)
                .pathParam("membership", membership)
                .when()
                .get("/clubs/?page={page}&page_size={page_size}&membership={membership}")
                .then()
                .spec(successfulBookClubsListResponseSpec)
                .extract()
                .as(BookClubsListResponseModel.class);
    }

    @Step("Регистрация членом клуба")
    public void bookClubMemberRegistration(
            String accessToken, int id) {
        given(membersBookClubRequestSpec)
                .header("Authorization", "Bearer " + accessToken)
                .pathParam("id", id)
                .when()
                .post("/clubs/{id}/members/me/")
                .then()
                .spec(membersBookClubRegistrationResponseSpec);
    }

    @Step("Выход из членства клуба")
    public void bookClubMemberDelete(
            String accessToken, int id) {
        given(membersBookClubRequestSpec)
                .header("Authorization", "Bearer " + accessToken)
                .pathParam("id", id)
                .when()
                .delete("/clubs/{id}/members/me/")
                .then()
                .spec(membersBookClubDeleteResponseSpec);
    }

    @Step("Оставление отзыва на книгу")
    public SuccessfulReviewsPostBookClubResponseModel bookClubReviewsPost(
            String accessToken, SuccessfulReviewsPostBookClubBodyModel body) {
        return given(reviewsBookClubRequestSpec)
                .header("Authorization", "Bearer " + accessToken)
                .body(body)
                .when()
                .post("/clubs/reviews/")
                .then()
                .spec(reviewsPostBookClubResponseSpec)
                .extract()
                .as(SuccessfulReviewsPostBookClubResponseModel.class);
    }

    @Step("Просмотр отзывов на книгу")
    public SuccessfulReviewsGetBookClubResponseModel getReviewsBookClub(int idClub, int page, int page_size) {
        return given(reviewsBookClubRequestSpec)
                .pathParam("idClub", idClub)
                .pathParam("page", page)
                .pathParam("page_size", page_size)
                .when()
                .get("/clubs/reviews/?club={idClub}&page={page}&page_size={page_size}")
                .then()
                .spec(reviewsGetBookClubResponseSpec)
                .extract()
                .as(SuccessfulReviewsGetBookClubResponseModel.class);
    }


}
