package specs.users.logout;

import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;

import static io.restassured.filter.log.LogDetail.ALL;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.Matchers.notNullValue;
import static specs.BaseSpec.baseRequestSpec;

public class LogoutSpec {
    public static RequestSpecification logoutRequestSpec = baseRequestSpec;

    public static ResponseSpecification successfulLogoutResponseSpec = new ResponseSpecBuilder()
            .log(ALL)
            .expectStatusCode(200)
            .build();

    public static ResponseSpecification unauthorizedUserResponseSpec = new ResponseSpecBuilder()
            .log(ALL)
            .expectStatusCode(401)
            .expectBody(matchesJsonSchemaInClasspath(
                    "schemas/users/logout/unauthorized_user_logout_response_schema.json"))
            .expectBody("detail", notNullValue())
            .expectBody("code", notNullValue())
            .build();

    public static ResponseSpecification emptyTokenLogoutResponseSpec = new ResponseSpecBuilder()
            .log(ALL)
            .expectStatusCode(400)
            .expectBody(matchesJsonSchemaInClasspath(
                    "schemas/users/logout/empty_token_logout_response_schema.json"))
            .expectBody("refresh", notNullValue())
            .build();


    public static ResponseSpecification emptyRequestBodyLogoutResponseSpec = new ResponseSpecBuilder()
            .log(ALL)
            .expectStatusCode(400)
            .expectBody(matchesJsonSchemaInClasspath(
                    "schemas/users/logout/empty_request_body_response_schema.json"))
            .expectBody("refresh", notNullValue())
            .build();


}
