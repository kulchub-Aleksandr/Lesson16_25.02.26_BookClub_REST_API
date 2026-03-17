package models.clubs.listBookClub;

import java.util.List;

public record BookClubsListResponseModel(
        Integer count,
        String next,
        String previous,
        List<ClubModel> results
) {
}