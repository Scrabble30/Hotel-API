package app.security.routes;

import app.Populator;
import app.config.AppConfig;
import app.config.HibernateConfig;
import dk.bugelhartmann.UserDTO;
import io.javalin.Javalin;
import jakarta.persistence.EntityManagerFactory;
import org.junit.jupiter.api.*;

import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.*;

class SecurityRoutesTest {

    private static final String BASE_URL = "http://localhost:7070/api/v1";

    private static Populator populator;
    private static Javalin app;

    private List<UserDTO> userDTOList;

    private static EntityManagerFactory emf;

    @BeforeAll
    static void beforeAll() {
        emf = HibernateConfig.getEntityManagerFactoryForTest();

        populator = new Populator(emf);
        app = AppConfig.startServer(7070, emf);
    }

    @BeforeEach
    void setUp() {
        userDTOList = populator.populateUsers();
    }

    @AfterEach
    void tearDown() {
        populator.cleanUpUsers();
    }

    @AfterAll
    static void afterAll() {
        AppConfig.stopServer(app);
    }

    String loginAccount(String username, String password) {
        UserDTO userDTO = new UserDTO(username, password);

        return given()
                .body(userDTO)
                .when()
                .post(BASE_URL + "/auth/login")
                .then()
                .statusCode(200)
                .extract()
                .path("token");
    }

    @Test
    void test() {
        given()
                .when()
                .get(BASE_URL + "/auth/test")
                .then()
                .statusCode(200);
    }

    @Test
    void login() {
        UserDTO userDTO = new UserDTO("User1", "user123");

        given()
                .body(userDTO)
                .when()
                .post(BASE_URL + "/auth/login")
                .then()
                .statusCode(200)
                .body("username", equalTo(userDTO.getUsername()))
                .body("token", is(notNullValue()));
    }

    @Test
    void register() {
        UserDTO userDTO = new UserDTO("User2", "user123");

        given()
                .body(userDTO)
                .when()
                .post(BASE_URL + "/auth/register")
                .then()
                .statusCode(201)
                .body("username", equalTo(userDTO.getUsername()))
                .body("token", is(notNullValue()));
    }

    @Test
    void protectedUser() {
        String token = loginAccount("User1", "user123");

        given()
                .header("Authorization", "Bearer " + token)
                .when()
                .get(BASE_URL + "/protected/user_demo")
                .then()
                .statusCode(200);
    }

    @Test
    void protectedUserUnauthorized() {
        given()
                .when()
                .get(BASE_URL + "/protected/user_demo")
                .then()
                .statusCode(401);
    }

    @Test
    void protectedAdmin() {
        String token = loginAccount("Admin1", "admin123");

        given()
                .header("Authorization", "Bearer " + token)
                .when()
                .get(BASE_URL + "/protected/admin_demo")
                .then()
                .statusCode(200);
    }

    @Test
    void protectedUnauthorized() {
        given()
                .when()
                .get(BASE_URL + "/protected/admin_demo")
                .then()
                .statusCode(401);
    }

    @Test
    void protectedUnauthorizedUser() {
        String token = loginAccount("User1", "user123");

        given()
                .header("Authorization", "Bearer " + token)
                .when()
                .get(BASE_URL + "/protected/admin_demo")
                .then()
                .statusCode(401);
    }
}