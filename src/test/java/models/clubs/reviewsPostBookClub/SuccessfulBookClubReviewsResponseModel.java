package models.clubs.reviewsPostBookClub;

import java.time.OffsetDateTime;


public record SuccessfulBookClubReviewsResponseModel(
        Integer id,
        Integer club,
        UserRecord user,
        String review,
        Integer assessment,
        Integer readPages,
        OffsetDateTime created,
        OffsetDateTime modified
) {
}

