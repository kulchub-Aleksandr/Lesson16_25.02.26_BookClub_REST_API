package specs.clubs.bookClubMembers;

import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;

import static io.restassured.filter.log.LogDetail.ALL;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.Matchers.notNullValue;
import static specs.BaseSpec.baseRequestSpec;

public class BookClubMembersSpec {
    public static RequestSpecification membersBookClubRequestSpec = baseRequestSpec;

    public static ResponseSpecification membersBookClubRegistrationResponseSpec = new ResponseSpecBuilder()
            .log(ALL)
            .expectStatusCode(204)
//            .expectBody(matchesJsonSchemaInClasspath(     "schemas/clubs/bookClubDelete/book_clubs_delete_response_schema.json"))
//            .expectBody("detail", notNullValue())
            .build();

    public static ResponseSpecification membersBookClubDeleteResponseSpec = new ResponseSpecBuilder()
            .log(ALL)
            .expectStatusCode(204)
//            .expectBody(matchesJsonSchemaInClasspath(     "schemas/clubs/bookClubDelete/book_clubs_delete_response_schema.json"))
//            .expectBody("detail", notNullValue())
            .build();
}