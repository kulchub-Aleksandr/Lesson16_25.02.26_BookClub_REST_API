package models.clubs.listMembershipBookClub;

import models.clubs.listBookClub.ClubModel;

import java.util.List;

public record BookClubsMembershipListResponseModel(
        Integer  count,
        String next,
        String previous,
        List<ClubModel> results
) {
}