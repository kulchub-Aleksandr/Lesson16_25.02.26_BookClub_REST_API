package models.users.update;


public record SuccessfulUpdateUserResponseModel(Integer id, String username, String firstName,
                                                String lastName, String email, String remoteAddr) {}
