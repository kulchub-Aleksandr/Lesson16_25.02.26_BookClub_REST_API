package models.clubs.localStorage;

public record LocalStorageAuthRequestBody (
        UserData userData,
        String accessToken,
        String refreshToken,
        boolean isAuthenticated
){
}
