package api;

import io.qameta.allure.Step;
import models.clubs.deleteBookClubs.PermissionUnsuccessfulBookClubDeleteResponseModel;
import models.clubs.registrationBookClub.SuccessfulBookClubRegistrationBodyModel;
import models.clubs.registrationBookClub.SuccessfulBookClubRegistrationResponseModel;


import static allure.CustomAllureListener.withCustomTemplate;
import static io.restassured.RestAssured.given;
import static specs.clubs.bookClubDelete.BookClubDeleteSpec.permissionUnsuccessfulBookClubDeleteResponseSpec;
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
        given()
                .log().all()
                .filter(withCustomTemplate())
                .header("Authorization", "Bearer " + accessToken)
                .pathParam("id", id)
                .when()
                .delete("/clubs/{id}/")
                .then()
                .log().all();
    }

    @Step("Удаление клуба с аккаунта не создателя клуба")
    public  PermissionUnsuccessfulBookClubDeleteResponseModel bookClubPermissionDelete (
            String accessToken, int id) {
        return given()
                .log().all()
                .filter(withCustomTemplate())
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

//    @Step("Получение списка клубов GET /clubs/")
//    public ClubsListResponseModel getClubs() {
//        return given(clubsRequestSpec)
//                .when()
//                .get("/clubs/")
//                .then()
//                .spec(successfulClubsListResponseSpec)
//                .extract()
//                .as(ClubsListResponseModel.class);
//    }
}
