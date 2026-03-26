package api;

import io.qameta.allure.Step;
import models.users.registration.*;
import models.users.update.*;

import static allure.CustomAllureListener.withCustomTemplate;
import static io.restassured.RestAssured.given;
import static specs.users.registration.RegistrationSpec.*;
import static specs.users.update.UpdateSpec.*;

public class UsersApiClient {

    @Step("Регистрация нового пользователя")
    public SuccessfulRegistrationResponseModel
    registration(RegistrationBodyModel body) {
        return given(registrationRequestSpec)
                .body(body)
                .when()
                .post("/users/register/")
                .then()
                .spec(successfulRegistrationResponseSpec)
                .extract()
                .as(SuccessfulRegistrationResponseModel.class);
    }
    @Step("Регистрация нового пользователя с уже существующими регистрационными данными и проверка ответа (400)")
    public ExistingUserResponseModel
    registrationExistingUse(RegistrationBodyModel body) {
        return given(registrationRequestSpec)
                .body(body)
                .when()
                .post("/users/register/")
                .then()
                .spec(existingUserRegistrationResponseSpec)
                .extract()
                .as(ExistingUserResponseModel.class);
    }

    @Step("Регистрация нового пользователя с невалидными регистрационными данными и проверка ответа (400)")
    public InvalidUserNameResponseModel
    registrationInvalidUserName(RegistrationBodyModel body) {
        return given(registrationRequestSpec)
                .body(body)
                .when()
                .post("/users/register/")
                .then()
                .spec(invalidUserNameRegistrationResponseSpec)
                .extract()
                .as(InvalidUserNameResponseModel.class);
    }

    @Step("Регистрация нового пользователя с неподдерживаемым типом передаваемых данных и проверка ответа (415)")
    public UnsupportedMediaTypeResponseModel
    registrationUnsupportedMediaType(RegistrationBodyModel body) {
        return given()
                .filter(withCustomTemplate())
                .log().all()
                .body(body)
                .when()
                .post("/users/register/")
                .then()
                .spec(unsupportedMediaTypeResponseSpec)
                .extract()
                .as(UnsupportedMediaTypeResponseModel.class);
    }


    @Step("Отправка запроса put с access-токеном и проверка ответа (200)")
    public SuccessfulUpdateUserResponseModel updateUser(
            String accessToken,
            UpdateBodyModel body) {
        return given(updateRequestSpec)
                .header("Authorization", "Bearer " + accessToken)
                .body(body)
                .when()
                .put("/users/me/")
                .then()
                .spec(successfulUpdateResponseSpec)
                .extract()
                .as(SuccessfulUpdateUserResponseModel.class);
    }

    @Step("Получение данных User методом get и проверка ответа (200)")
    public SuccessfulUpdateUserResponseModel getUserData(String accessToken) {
        return given(updateRequestSpec)
                .header("Authorization", "Bearer " + accessToken)
                .when()
                .get("/users/me/")
                .then()
                .spec(updatedUserDataResponseSpec)
                .extract()
                .as(SuccessfulUpdateUserResponseModel.class);
    }

    @Step("Отправка запроса put без предварительной аутентификации и проверка ответа (401)")
    public NotProvidedAuthenticationCredentialsResponseModel
    updateNotProvidedAuthenticationCredentials(UpdateBodyModel body) {
        return given(updateRequestSpec)
                .body(body)
                .when()
                .put("/users/me/")
                .then()
                .spec(notProvidedAuthenticationCredentialsResponseSpec)
                .extract()
                .as(NotProvidedAuthenticationCredentialsResponseModel.class);

    }

    @Step("Отправка запроса patch с access-токеном и проверка ответа (200)")
    public SuccessfulUpdateUserResponseModel
    updateWithPatch(String accessToken,
                    PartialUpdateBodyModel body) {
        return given(updateRequestSpec)
                .header("Authorization", "Bearer " + accessToken)
                .body(body)
                .when()
                .patch("/users/me/")
                .then()
                .spec(successfulUpdateResponseSpec)
                .extract()
                .as(SuccessfulUpdateUserResponseModel.class);
    }

    @Step("Отправка запроса put с access-токеном и проверка ответа (400)")
    public PartialWithPutMethodUpdateUserResponseModel
    updatePartialWithPut(String accessToken,
                         PartialUpdateBodyModel body) {
        return given(updateRequestSpec)
                .header("Authorization", "Bearer " + accessToken)
                .body(body)
                .when()
                .put("/users/me/")
                .then()
                .spec(partialWithPutMethodUpdateResponseSpec)
                .extract()
                .as(PartialWithPutMethodUpdateUserResponseModel.class);

    }

    @Step("Удаление пользователя без авторизации и проверка ответа (401)")
    public NotProvidedAuthenticationCredentialsResponseModel
    deleteUserUnauthorized() {
        return given(updateRequestSpec)
                .when()
                .delete("/users/me/")
                .then()
                .spec(notProvidedAuthenticationCredentialsResponseSpec)
                .extract()
                .as(NotProvidedAuthenticationCredentialsResponseModel.class);


    }

    @Step("Удаление пользователя")
    public void deleteUserAuthorized(String accessToken) {
        given(updateRequestSpec)
                .header("Authorization", "Bearer " + accessToken)
                .when()
                .delete("/users/me/")
                .then()
                .log().all()
                .statusCode(204);
    }

}
