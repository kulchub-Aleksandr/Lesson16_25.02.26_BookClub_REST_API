package specs.update;

import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import org.hamcrest.Matchers;

import static io.restassured.RestAssured.with;
import static io.restassured.filter.log.LogDetail.ALL;
import static io.restassured.http.ContentType.JSON;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.Matchers.notNullValue;

public class UpdateSpec {
    public static RequestSpecification updateRequestSpec = with()
            .log().all()
            .contentType(JSON);

    public static ResponseSpecification successfulUpdateResponseSpec = new ResponseSpecBuilder()
            .log(ALL)
            .expectStatusCode(200)
            .expectBody(matchesJsonSchemaInClasspath(
                    "schemas/update/successful_update_response_schema.json"))
            .expectBody("username", notNullValue())
            .expectBody("firstName", notNullValue())
            .expectBody("lastName", notNullValue())
            .expectBody("email", notNullValue())
            .build();

    public static ResponseSpecification notProvidedAuthenticationCredentialsResponseSpec = new ResponseSpecBuilder()
            .log(ALL)
            .expectStatusCode(401)
            .expectBody(matchesJsonSchemaInClasspath(
                    "schemas/update/not_provided_authentication_credentials_update_response_schema.json"))
            .expectBody("detail", notNullValue())
            .expectHeader("WWW-Authenticate", Matchers.containsString("Bearer realm=\"api\""))
            .build();

    public static ResponseSpecification emptyTokenLogoutResponseSpec = new ResponseSpecBuilder()
            .log(ALL)
            .expectStatusCode(400)
            .expectBody(matchesJsonSchemaInClasspath(
                    "schemas/logout/empty_token_logout_response_schema.json"))
            .expectBody("refresh", notNullValue())
            .build();


    public static ResponseSpecification emptyRequestBodyLogoutResponseSpec = new ResponseSpecBuilder()
            .log(ALL)
            .expectStatusCode(400)
            .expectBody(matchesJsonSchemaInClasspath(
                    "schemas/logout/empty_request_body_response_schema.json"))
            .expectBody("refresh", notNullValue())
            .build();

    public static ResponseSpecification emptyUserEmptyPasswordLoginResponseSpec = new ResponseSpecBuilder()
            .log(ALL)
            .expectStatusCode(400)
            .expectBody(matchesJsonSchemaInClasspath(
                    "schemas/login/empty_user_empty_password_login_response_schema.json"))
            .expectBody("username", notNullValue())
            .expectBody("password", notNullValue())
            .build();


}
