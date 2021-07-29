import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.when;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchema;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;

import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import java.io.File;
import org.junit.jupiter.api.Test;


public class GetUserTests extends TestConfig {

    @Test
    public void getAllUsers() {
        when()
            .get(users)
        .then()
            .statusCode(200);
    }

    @Test
    public void getUserInvalidCredentials() {
        String userId = "123";
        when()
            .get(users.concat("/").concat(userId))
        .then()
            .statusCode(200)
        .and()
            .rootPath("data")
            .body("code", equalTo(401))
            .body("name", equalTo("Unauthorized"))
        .and()
            .log()
            .all();
    }

    @Test
    public void getUserOAuth() {
        String userId = "125155161616";

        given()
            .spec(requestSpec)
        .and()
            .pathParam("userId", userId)
        .when()
            .get(usersById)
        .then()
            .spec(responseOkSpec)
        .and()
            .body("code", equalTo(404))
            .body("data.message", equalTo("Resource not found"));
    }

    @Test
    public void getUserBasicAuth() {
        String userId = "456";

        given()
            .accept(ContentType.JSON)
            .auth()
            .basic("userName", "password")
        .when()
            .get(users.concat("/").concat(userId))
        .then()
            .statusCode(200)
        .and()
            .body("code", equalTo(401))
            .body("message", equalTo("Your request was made with invalid credentials."))
        .log()
        .body();
    }

    @Test
    public void getAllUsersFirstNameWilfordVerifyEmail() {
        given()
            .spec(requestSpec)
        .when()
            .get(users)
        .then()
            .statusCode(200)
        .and()
            .log()
            .ifValidationFails(LogDetail.BODY)
        .and()
            .body("data.findAll {it.name.equals('Swara Trivedi')}.email", hasItem("trivedi_swara@hermiston.info"));
    }

    @Test
    public void headersTest() {
        given()
            .accept(ContentType.JSON)
        .and()
            .header("my_header", "my_value")
            .cookie("my_cookie", "my_cookie_value")
        .and()
            .auth()
            .oauth2(apiToken)
        .and()
            .log()
            .all()
        .when()
            .get(posts)
        .then()
            .header("Content-Encoding", equalTo("gzip"));

    }

    @Test
    public void queryParamsTest() {
        given()
            .accept(ContentType.JSON)
        .and()
            .auth()
            .oauth2(apiToken)
        .and()
            .param("name", "Wilford")
        .when()
            .get(users)
        .then()
            .log()
            .ifValidationFails(LogDetail.BODY)
        .and()
            .statusCode(200)
        .and()
            .body("data.findAll { !it.name.equals('Wilford') }", hasSize(0));
    }

    @Test
    public void pathParamTest() {
        String userId = "815";

        given()
            .accept(ContentType.JSON)
        .and()
            .auth()
            .oauth2(apiToken)
        .and()
            .pathParam("userId", userId)
        .when()
            .get(usersById)
        .then()
            .log()
            .body()
        .and()
            .statusCode(200)
        .and()
            .body("data.name", equalTo("Mrs. Durgeshwari Nambeesan"));
    }

    @Test
    public void jsonSchemaTest() {
        File jsonSchema = new File(System.getProperty("user.dir") + "/src/test/resources/get-users-json-schema.json");

        given()
            .accept( ContentType.JSON)
        .and()
            .auth()
            .oauth2(apiToken)
        .when()
            .get(users)
        .then()
            .body(matchesJsonSchema(jsonSchema));
    }
}
