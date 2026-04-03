package pages.component;

import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Condition.text;

public class CheckReviewsComponent {
    public void checkReviewResultValues(SelenideElement key, String value) {
        key.shouldHave(text(value));

    }
}
