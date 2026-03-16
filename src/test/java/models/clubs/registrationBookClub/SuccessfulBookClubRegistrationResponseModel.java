package models.clubs.registrationBookClub;


import java.time.OffsetDateTime;
import java.util.List;

public record SuccessfulBookClubRegistrationResponseModel(
        Integer id,
        String bookTitle,
        String bookAuthors,
        Integer publicationYear,
        String description,
        String telegramChatLink,
        Integer owner,
        List<Integer> members,
        List<ReviewModel> reviews,
        OffsetDateTime created,
        OffsetDateTime modified
) {
}

record ReviewModel(
        Integer id,
        Integer club,
        UserModel user,
        String review,
        Integer assessment,
        Integer readPages,
        OffsetDateTime created,
        OffsetDateTime modified
) {
}

record UserModel(
        Integer id,
        String username
) {
}