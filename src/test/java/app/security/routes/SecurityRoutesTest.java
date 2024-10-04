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
}