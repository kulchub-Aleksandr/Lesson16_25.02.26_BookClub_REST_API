package pages;

import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Selenide.$;

public class ClubPage {
    private final SelenideElement addReviewButton = $(".add-review-btn"); //создаем переменные для хранения локаторов
    private final SelenideElement assessmentInput = $("#assessment");
    private final SelenideElement readPagesInput = $("#readPages");
    private final SelenideElement reviewInput = $("#review");
    private final SelenideElement saveButton = $(".save-btn");
    private final SelenideElement reviewerName = $(".reviewer-name");
    private final SelenideElement reviewRating = $(".review-rating");
    private final SelenideElement readPages = $(".read-pages");
    private final SelenideElement reviewContent = $(".review-content");
    private final SelenideElement reviewDate = $(".review-content");
    private final SelenideElement editReviewButton = $(".edit-review-btn");
    private final SelenideElement deleteReviewButton = $(".delete-review-btn");
    private final SelenideElement review = $(".review-card.user-review");
}
