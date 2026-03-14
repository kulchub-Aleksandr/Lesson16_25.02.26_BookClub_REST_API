package tests;

import net.datafaker.Faker;
import net.datafaker.providers.base.Text;

import static net.datafaker.providers.base.Text.DIGITS;
import static net.datafaker.providers.base.Text.EN_UPPERCASE;

public class TestData {
    private final Faker faker = new Faker();

    public String getUsername() {
        return faker.name().firstName();
    }

    public String getPassword() {
        return  faker.text().text(Text.TextSymbolsBuilder.builder()
                .len(8).with(EN_UPPERCASE, 2)
                .with(DIGITS, 3)
                .build()
        );
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
        return faker.book().title();
    }

    public String getBookAuthor() {
        return faker.book().author();
    }

    public int getPublicationYear (){
        return faker.number().numberBetween(1800, java.time.Year.now().getValue());
    }

    public String getBookDescription() {
        return faker.book().title();
    }

    public String getTelegramChatLink() {
        return faker.internet().url();
    }

}

