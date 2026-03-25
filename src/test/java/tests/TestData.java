package tests;

import net.datafaker.Faker;

import java.util.Random;

public class TestData {
    private final Faker faker = new Faker();
    private final Random random = new Random();

    public String getUsername() {
        String baseName = faker.name().firstName();
        int suffix = random.nextInt(9000) + 1000;
        return baseName + suffix;
    }

    public String getWrongUsername() {
        return faker.name().fullName();
    }


    public String getPassword() {
        return
                faker.credentials().password();
    }

    public String getFirstName() {
        return faker.name().firstName();
    }

    public String getLastName() {
        return faker.name().lastName();
    }

    public String getEmail() {
        return faker.internet().emailAddress();
    }

    public String getEmptyPassword() {
        return "";
    }

    public String getEmptyUsername() {
        return "";
    }

    public String getBookTitle() {
        String baseTitle = faker.book().title();
        int suffix = random.nextInt(9000) + 1000;
        return baseTitle + suffix;
    }

    public String getBookAuthor() {
        return faker.book().author();
    }

    public int getPublicationYear() {
        return faker.number().numberBetween(1800, java.time.Year.now().getValue());
    }

    public String getBookDescription() {
        return faker.book().title();
    }

    public String getTelegramChatLink() {
        return faker.internet().url();
    }

}

