package pages;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import pages.component.CheckReviewsComponent;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.*;

public class ClubPage {
    private final SelenideElement
            addReviewButton = $(".add-review-btn"),
            clubContent = $(".club-content"),
            assessmentInput = $("#assessment"),
            readPagesInput = $("#readPages"),
            reviewInput = $("#review"),
            saveReviewButton = $(".save-btn"),
            reviewerName = $(".reviewer-name"),
            reviewRating = $(".review-rating"),
            readPages = $(".read-pages"),
            reviewContent = $(".review-content"),
            reviewDate = $(".review-content"),
            editReviewButton = $(".edit-review-btn"),
            deleteReviewButton = $(".delete-review-btn"),
            //review = $(".review-card.user-review");
            review = $(".user-review");

    CheckReviewsComponent checkReviews = new CheckReviewsComponent();


    @Step("Открыть ресурс и передать авторизацию {value}")
    public ClubPage openPage(String value) {
        open("/favicon.ico");
        localStorage().setItem("book_club_auth", value);
        return this;
    }

    @Step("Открыть форму созданного клуба {value}")
    public ClubPage openClubPage(int value) {
        open("/clubs/" + value);
        return this;
    }

    @Step("Проверка что карточка клуба появилась на экране")
    public ClubPage clubContentCheck() {
        clubContent.shouldBe(visible);
        return this;
    }

    @Step("Нажатие кнопки 'Написать отзыв'")
    public ClubPage pressReviewButton() {
        addReviewButton.click();
        return this;
    }

    @Step("Нажатие кнопки 'Редактировать' отзыв")
    public ClubPage pressEditReviewButton() {
        editReviewButton.shouldBe(visible);
        editReviewButton.click();
        return this;
    }

    @Step("Ввод оценки {value}")
    public ClubPage setAssessment(int value) {
        assessmentInput.clear();
        assessmentInput.setValue(String.valueOf(value));
        return this;
    }

    @Step("Ввод количества прочитанных страниц {value}")
    public ClubPage setReadPages(int value) {
        readPagesInput.setValue(String.valueOf(value));
        return this;
    }

    @Step("Ввод текста обзора {value}")
    public ClubPage setReview(String value) {
        reviewInput.clear();
        reviewInput.setValue(value);
        return this;
    }

    @Step("Нажатие кнопки 'Опубликовать'")
    public ClubPage saveReviewButton() {
        saveReviewButton.click();
        return this;
    }

    @Step("Проверить, что значение поля {key} содержит ожидаемое {value}")
    public ClubPage checkResult(SelenideElement key, String value) {
        checkReviews.checkReviewResultValues(key, value);
        return this;
    }

    public SelenideElement getReviewerName() {
        return reviewerName;
    }

    public SelenideElement getReview() {
        return review;
    }

    public SelenideElement getReadPages() {
        return readPages;
    }

    @Step("Нажатие кнопки 'Удалить' отзыв")
    public ClubPage deleteReview() {
        deleteReviewButton.click();
        return this;
    }
    @Step("Нажатие кнопки 'Удалить' отзыв")
    public ClubPage deleteCheckReview() {
        review.shouldNotBe(visible);
        return this;
    }




}
