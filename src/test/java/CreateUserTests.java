import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

import com.github.javafaker.Faker;
import dao.BaseResponse;
import dao.CreateUser;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.junit.jupiter.api.Test;


public class CreateUserTests extends TestConfig {

    Faker faker = new Faker();

    @Test
    public void createUserExtractResponse() throws IOException {

        String userJson = new String(Files.readAllBytes(Paths.get(System.getProperty("user.dir") + "/src/test/resources/testUser.json")));

        // Create new user
        Response response = (Response) given()
                .contentType(ContentType.JSON)
            .and()
                .auth()
                .oauth2(apiToken)
            .and()
                .body(userJson)
            .when()
                .post(users)
            .then()
                .log()
                .body()
            .and()
                .body("code", equalTo(201))
            .extract();

        JsonPath responseJsonPath = new JsonPath(response.getBody().asString()).setRootPath("data");
        String id = responseJsonPath.getString("id");

        JsonPath requestJsonPath = new JsonPath(userJson);
        String expectedName = requestJsonPath.getString("name");
        String expectedGender = requestJsonPath.getString("gender");

        System.out.println(id);

        //Get the created user
        given()
            .accept(ContentType.JSON)
        .and()
            .auth()
            .oauth2(apiToken)
        .and()
            .pathParam("userId", id)
        .when()
            .get(usersById)
        .then()
            .log()
            .body()
        .and()
            .rootPath("data")
            .body("name", equalTo(expectedName))
            .body("gender", equalTo(expectedGender));

        //Delete the created user
        given()
            .accept(ContentType.JSON)
        .and()
            .auth()
            .oauth2(apiToken)
        .and()
            .pathParam("userId",id)
        .when()
            .delete(usersById)
        .then()
            .log()
            .body()
        .and()
            .body("code", equalTo(204));

    }

    @Test
    public void createUserDeserializeResponse() {
        CreateUser createUser = new CreateUser();
        createUser.setName(faker.name().fullName());
        createUser.setGender("Male");
        createUser.setStatus("Active");
        createUser.setEmail(faker.internet().emailAddress());

        BaseResponse response = given()
            .contentType(ContentType.JSON)
            .and()
                .auth()
                .oauth2(apiToken)
            .and()
                .body(createUser)
            .when()
                .post(users)
            .then()
                .log()
                .body()
            .and()
                .body("code", equalTo(201))
            .extract()
            .as(BaseResponse.class);

        assert response.getData().getName().equals(createUser.getName());
        assert response.getData().getGender().equals(createUser.getGender());
        assert response.getData().getStatus().equals(createUser.getStatus());
        assert response.getData().getEmail().equals(createUser.getEmail());

    }
}
