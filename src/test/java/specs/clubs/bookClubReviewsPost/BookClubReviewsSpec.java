package specs.clubs.bookClubReviewsPost;

import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;

import static io.restassured.filter.log.LogDetail.ALL;
import static specs.BaseSpec.baseRequestSpec;

public class BookClubReviewsSpec {
    public static RequestSpecification reviewsBookClubRequestSpec = baseRequestSpec;

    public static ResponseSpecification reviewsPostBookClubResponseSpec = new ResponseSpecBuilder()
            .log(ALL)
            .expectStatusCode(201)
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