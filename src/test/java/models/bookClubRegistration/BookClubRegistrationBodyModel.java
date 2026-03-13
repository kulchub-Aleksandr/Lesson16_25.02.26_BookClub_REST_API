package models.bookClubRegistration;


public record BookClubRegistrationBodyModel(
        String bookTitle,
        String bookAuthors,
        Integer publicationYear,
        String description,
        String telegramChatLink
) {}
