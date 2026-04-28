package tests;

import allure.Attach;
import api.ApiClient;
import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.WebDriverRunner;
import com.codeborne.selenide.logevents.SelenideLogger;
import config.WebConfig;
import io.qameta.allure.selenide.AllureSelenide;
import io.restassured.RestAssured;
import org.aeonbits.owner.ConfigFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.util.Map;

import static com.codeborne.selenide.Selenide.closeWebDriver;

public class TestBase {

    protected static final ApiClient api = new ApiClient();
    private static final WebConfig webConfig = ConfigFactory.create(WebConfig.class, System.getProperties());

    @BeforeAll
    public static void setUp() {
        //Configuration.browserSize = System.getProperty("browserSize");
        Configuration.browserSize = webConfig.browserSize();

        //RestAssured.baseURI = "https://book-club.qa.guru";
        RestAssured.baseURI = webConfig.baseURI();
        //RestAssured.basePath = "/api/v1";
        RestAssured.basePath = webConfig.basePath();

        //Configuration.baseUrl = System.getProperty("baseUrl", "https://book-club.qa.guru");
        Configuration.baseUrl = webConfig.baseUrl();
       // Configuration.browser = System.getProperty("browser", "chrome");
        Configuration.browser = webConfig.browser();
       // Configuration.browserVersion = System.getProperty("browserVersion");
        Configuration.browserVersion = webConfig.browserVersion();
        Configuration.pageLoadStrategy = "eager";
        //Configuration.holdBrowserOpen = true;
        Configuration.timeout = 10000; // default 4000


        DesiredCapabilities capabilities = new DesiredCapabilities();
        capabilities.setCapability("selenoid:options", Map.<String, Object>of(
                "enableVNC", true,
                "enableVideo", true
        ));
        Configuration.browserCapabilities = capabilities;
        //Configuration.remote = System.getProperty("remoteUrl");
        Configuration.remote = webConfig.remoteUrl();
    }

    @BeforeEach
    void addListener() {
        SelenideLogger.addListener("allure", new AllureSelenide());
    }

    @AfterEach
    void addAttachments() {
        if (WebDriverRunner.hasWebDriverStarted()) {
            Attach.screenshotAs("Last screenshot");
            Attach.pageSource();
            Attach.browserConsoleLogs();
            Attach.addVideo();

            closeWebDriver();

        }
    }
}
