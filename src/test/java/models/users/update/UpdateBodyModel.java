package models.users.update;


public record UpdateBodyModel(String username,
                              String firstName,
                              String lastName,
                              String email) {}
