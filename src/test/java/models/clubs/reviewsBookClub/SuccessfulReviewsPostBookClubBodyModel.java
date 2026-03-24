package models.clubs.reviewsBookClub;


public record SuccessfulReviewsPostBookClubBodyModel(
        Integer club,
        String review,
        Integer assessment,
        Integer readPages
) {
}
