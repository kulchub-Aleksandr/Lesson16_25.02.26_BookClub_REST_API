package config;

import org.aeonbits.owner.Config;

@Config.Sources({
        "classpath:properties/${env}.properties",

})
public interface WebConfig extends Config {

    @Key("browser")
    @DefaultValue("CHROME")
    String browser();

    @Key("browserVersion")
    @DefaultValue("147")
    String browserVersion();

    @Key("browserSize")
    @DefaultValue("1920x1080")
    String browserSize();

    @Key("baseUrl")
    @DefaultValue("https://book-club.qa.guru")
    String baseUrl();

    @Key("isRemote")
    @DefaultValue("false")
    boolean isRemote();

    @Key("remoteUrl")
    String remoteUrl();

    @Key("baseURI")
    @DefaultValue("https://book-club.qa.guru")
    String baseURI();

    @Key("basePath")
    @DefaultValue("/api/v1")
    String basePath();


}
