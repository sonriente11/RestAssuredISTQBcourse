import static org.hamcrest.Matchers.lessThan;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.filter.log.ErrorLoggingFilter;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import io.restassured.parsing.Parser;
import io.restassured.path.json.JsonPath;
import io.restassured.path.json.config.JsonPathConfig;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.BeforeEach;

public class TestConfig {

    protected static String apiToken;
    protected static String usersById;
    protected static String users;
    protected static String posts;
    protected static String comments;
    protected static String albums;
    protected static String photos;

    protected RequestSpecification requestSpec;
    protected ResponseSpecification responseOkSpec;

    @BeforeEach
    public void setup() throws IOException {
        RestAssured.baseURI = "https://gorest.co.in/public-api";
        JsonPath.config = new JsonPathConfig("UTF-8");
        RestAssured.defaultParser = Parser.JSON;

        users = "/users";
        usersById = "/users/{userId}";
        posts = "/posts";
        comments = "/comments";
        albums = "/albums";
        photos = "/photos";
        apiToken = getApiToken();
        requestSpec = getRequestSpecification();
        responseOkSpec = getResponseSpecification();
    }

    private String getApiToken() throws IOException {
        InputStream inputStream = getClass().getClassLoader()
            .getResourceAsStream("authentication.properties");

        Properties prop = new Properties();
        prop.load(inputStream);
        return prop.getProperty("token");
    }

    private RequestSpecification getRequestSpecification() {
        RequestSpecBuilder builder = new RequestSpecBuilder();
        builder.addHeader("my-custom-header", "my-custom-header-value");
        builder.setAccept(ContentType.JSON);
        builder.setAuth(RestAssured.oauth2(apiToken));
        builder.log(LogDetail.HEADERS);
        builder.addFilter(new ErrorLoggingFilter());
        return builder.build();
    }

    private ResponseSpecification getResponseSpecification() {
        ResponseSpecBuilder builder = new ResponseSpecBuilder();
        builder.expectContentType(ContentType.JSON);
        builder.expectStatusCode(200);
        builder.expectResponseTime(lessThan(2L), TimeUnit.SECONDS);
        builder.log(LogDetail.BODY);
        return builder.build();
    }
}
