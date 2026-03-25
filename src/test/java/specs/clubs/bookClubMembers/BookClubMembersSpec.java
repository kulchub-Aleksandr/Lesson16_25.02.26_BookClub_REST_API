package specs.clubs.bookClubMembers;

import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;

import static io.restassured.filter.log.LogDetail.ALL;
import static specs.BaseSpec.baseRequestSpec;

public class BookClubMembersSpec {
    public static RequestSpecification membersBookClubRequestSpec = baseRequestSpec;

    public static ResponseSpecification membersBookClubRegistrationResponseSpec = new ResponseSpecBuilder()
            .log(ALL)
            .expectStatusCode(204)
            .build();

    public static ResponseSpecification membersBookClubDeleteResponseSpec = new ResponseSpecBuilder()
            .log(ALL)
            .expectStatusCode(204)
            .build();
}