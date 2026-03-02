package models.update;


import java.util.List;

public record PartialWithPutMethodUpdateUserResponseModel(List<String> username, List<String> email) {}
