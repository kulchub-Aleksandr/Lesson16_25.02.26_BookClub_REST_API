package models.logout;

import java.util.List;

public record EmptyTokenLogoutResponseModel(List<String> refresh) {}