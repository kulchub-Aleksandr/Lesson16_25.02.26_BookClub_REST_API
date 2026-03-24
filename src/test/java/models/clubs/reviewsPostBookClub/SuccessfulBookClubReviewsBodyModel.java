package models.clubs.reviewsPostBookClub;


public record SuccessfulBookClubReviewsBodyModel(
        Integer club,
        String review,
        Integer assessment,
        Integer readPages
) {
}
