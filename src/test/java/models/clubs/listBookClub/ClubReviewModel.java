package models.clubs.listBookClub;

/**
 * Отзыв на клуб (вложенная структура в ответе списка клубов).
 */
public record ClubReviewModel(
        Integer id,
        Integer club,
        ClubReviewUserModel user,
        String review,
        Integer assessment,
        Integer readPages,
        String created,
        String modified
) {}
