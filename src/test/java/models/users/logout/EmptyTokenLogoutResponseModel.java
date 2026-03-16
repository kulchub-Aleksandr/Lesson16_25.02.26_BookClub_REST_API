package models.users.logout;

import java.util.List;

public record EmptyTokenLogoutResponseModel(List<String> refresh) {}