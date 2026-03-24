package api;

import io.qameta.allure.Step;
import models.clubs.deleteBookClub.PermissionUnsuccessfulBookClubDeleteResponseModel;
import models.clubs.listBookClub.BookClubsListResponseModel;
import models.clubs.registrationBookClub.SuccessfulBookClubRegistrationBodyModel;
import models.clubs.registrationBookClub.SuccessfulBookClubRegistrationResponseModel;


import static allure.CustomAllureListener.withCustomTemplate;
import static io.restassured.RestAssured.given;
import static specs.clubs.bookClubDelete.BookClubDeleteSpec.permissionBookClubDeleteRequestSpec;
import static specs.clubs.bookClubDelete.BookClubDeleteSpec.permissionUnsuccessfulBookClubDeleteResponseSpec;
import static specs.clubs.bookClubList.BookClubListSpec.*;
import static specs.clubs.bookClubRegistration.BookClubRegistrationSpec.bookClubRegistrationRequestSpec;
import static specs.clubs.bookClubRegistration.BookClubRegistrationSpec.successfulBookClubRegistrationResponseSpec;


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
    public SuccessfulBookClubRegistrationBodyModel getClubById(String accessToken, int id) {
        return given(clubsRequestSpec)
                .header("Authorization", "Bearer " + accessToken)
                .pathParam("id", id)
                .when()
                .get("/clubs/{id}/")
                .then()
                .spec(successfulBookClubListGetResponseSpec)
                .extract()
                .as(SuccessfulBookClubRegistrationBodyModel.class);
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

    @Step("Выход из клуба")
    public void bookClubMemberDelete(
            String accessToken, int id) {
        given(permissionBookClubDeleteRequestSpec)
                .header("Authorization", "Bearer " + accessToken)
                .pathParam("id", id)
                .when()
                .delete("/clubs/{id}/members/me/")
                .then()
                .log().all();
    }


}
