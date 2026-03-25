package specs.clubs.bookClubDelete;

import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;

import static io.restassured.filter.log.LogDetail.ALL;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.Matchers.notNullValue;
import static specs.BaseSpec.baseRequestSpec;

public class BookClubDeleteSpec {
    public static RequestSpecification permissionBookClubDeleteRequestSpec = baseRequestSpec;

    public static ResponseSpecification permissionUnsuccessfulBookClubDeleteResponseSpec = new ResponseSpecBuilder()
            .log(ALL)
            .expectStatusCode(403)
            .expectBody(matchesJsonSchemaInClasspath("schemas/clubs/bookClubDelete/book_clubs_delete_response_schema.json"))
            .expectBody("detail", notNullValue())
            .build();
}