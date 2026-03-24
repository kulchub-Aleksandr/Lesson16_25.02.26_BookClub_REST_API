package models.clubs.reviewsBookClub;

import java.util.List;


public record SuccessfulReviewsGetBookClubResponseModel(
        Integer count,
        String next,
        String previous,
        List<ReviewItemModel> results
) {
}

