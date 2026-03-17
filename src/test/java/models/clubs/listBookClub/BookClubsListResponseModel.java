package models.clubs.listBookClub;

import java.util.List;

public record BookClubsListResponseModel(
        Integer count,
        String next,
        String previous,
        List<ClubModel> results
) {}

 record ClubModel(
        Integer id,
        String bookTitle,
        String bookAuthors,
        Integer publicationYear,
        String description,
        String telegramChatLink,
        Integer owner,
        List<Integer> members,
        List<ClubReviewModel> reviews,
        String created,
        String modified
) {}

 record ClubReviewModel(
        Integer id,
        Integer club,
        ClubReviewUserModel user,
        String review,
        Integer assessment,
        Integer readPages,
        String created,
        String modified
) {}

 record ClubReviewUserModel(Integer id,
                            String username
 ) {}
