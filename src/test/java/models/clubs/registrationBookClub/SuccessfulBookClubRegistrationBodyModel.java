package models.clubs.registrationBookClub;


public record SuccessfulBookClubRegistrationBodyModel(
        String bookTitle,
        String bookAuthors,
        Integer publicationYear,
        String description,
        String telegramChatLink
) {}
