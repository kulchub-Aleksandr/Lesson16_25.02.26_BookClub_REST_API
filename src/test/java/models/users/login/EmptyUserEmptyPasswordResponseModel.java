package models.users.login;

import java.util.List;

public record EmptyUserEmptyPasswordResponseModel(List<String> username, List<String> password) {}
