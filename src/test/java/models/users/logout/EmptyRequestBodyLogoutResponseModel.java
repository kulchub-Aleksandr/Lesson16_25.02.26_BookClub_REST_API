package models.users.logout;

import java.util.List;

public record EmptyRequestBodyLogoutResponseModel(List<String> refresh) {}