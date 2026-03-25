package specs.clubs.bookClubReviewsPost;

import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;

import static io.restassured.filter.log.LogDetail.ALL;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.Matchers.notNullValue;
import static specs.BaseSpec.baseRequestSpec;

public class BookClubReviewsSpec {
    public static RequestSpecification reviewsBookClubRequestSpec = baseRequestSpec;

    public static ResponseSpecification reviewsPostBookClubResponseSpec = new ResponseSpecBuilder()
            .log(ALL)
            .expectStatusCode(201)
            .expectBody(matchesJsonSchemaInClasspath(
                    "schemas/clubs/bookClubReviews/successful_book_club_post_reviews_response_schema.json"))
            .expectBody("id", notNullValue())
            .expectBody("review", notNullValue())
            .expectBody("club", notNullValue())
            .build();

    public static ResponseSpecification reviewsGetBookClubResponseSpec = new ResponseSpecBuilder()
            .log(ALL)
            .expectStatusCode(200)
            .expectBody(matchesJsonSchemaInClasspath(
                    "schemas/clubs/bookClubReviews/successful_book_club_get_reviews_response_schema.json"))
            .expectBody("count", notNullValue())
            .build();
}