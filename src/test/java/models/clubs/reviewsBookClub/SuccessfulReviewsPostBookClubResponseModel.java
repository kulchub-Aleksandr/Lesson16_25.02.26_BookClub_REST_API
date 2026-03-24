package models.clubs.reviewsBookClub;

import java.time.OffsetDateTime;


public record SuccessfulReviewsPostBookClubResponseModel(
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

