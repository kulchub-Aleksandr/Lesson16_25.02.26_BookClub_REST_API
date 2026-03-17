package models.clubs.listBookClub;

import java.util.List;

public record ClubModel(
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
