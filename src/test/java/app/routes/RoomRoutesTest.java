package app.routes;

import app.Populator;
import app.config.AppConfig;
import app.config.HibernateConfig;
import app.dtos.HotelDTO;
import app.dtos.RoomDTO;
import io.javalin.Javalin;
import jakarta.persistence.EntityManagerFactory;
import org.junit.jupiter.api.*;

import java.util.List;
import java.util.Set;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.arrayContainingInAnyOrder;

class RoomRoutesTest {

    private static final String BASE_URL = "http://localhost:7070/api/v1";

    private static Populator populator;
    private static Javalin app;

    private List<HotelDTO> hotelDTOList;
    private List<RoomDTO> roomDTOList;

    @BeforeAll
    static void beforeAll() {
        EntityManagerFactory emf = HibernateConfig.getEntityManagerFactoryForTest();

        populator = new Populator(emf);
        app = AppConfig.startServer(7070, emf);
    }

    @BeforeEach
    void setUp() {
        hotelDTOList = populator.populateHotelsWithRooms();
        roomDTOList = hotelDTOList.stream().map(HotelDTO::getRooms).flatMap(Set::stream).toList();
    }

    @AfterEach
    void tearDown() {
        populator.cleanUpHotels();
    }

    @AfterAll
    static void afterAll() {
        AppConfig.stopServer(app);
    }

    @Test
    void createRoom() {
        RoomDTO expected = new RoomDTO(
                null,
                hotelDTOList.get(1).getId(),
                2,
                230.0
        );

        RoomDTO actual = given()
                .body(expected)
                .when()
                .post(BASE_URL + "/rooms")
                .then()
                .statusCode(201)
                .extract()
                .as(RoomDTO.class);

        expected.setId(actual.getId());

        assertThat(actual, equalTo(expected));
    }

    @Test
    void getRoomById() {
        RoomDTO expected = roomDTOList.get(1);

        RoomDTO actual = given()
                .when()
                .get(BASE_URL + "/rooms/{id}", expected.getId())
                .then()
                .statusCode(200)
                .extract()
                .as(RoomDTO.class);

        assertThat(actual, equalTo(expected));
    }

    @Test
    void getAllRooms() {
        List<RoomDTO> expected = roomDTOList;

        RoomDTO[] actual = given()
                .when()
                .get(BASE_URL + "/rooms")
                .then()
                .statusCode(200)
                .extract()
                .as(RoomDTO[].class);

        //assertThat(actual, equalTo(expected.toArray()));
        assertThat(actual, arrayContainingInAnyOrder(expected.toArray()));
    }

    @Test
    void updateRoom() {
        RoomDTO roomDTO = roomDTOList.get(1);
        RoomDTO expected = new RoomDTO(
                roomDTO.getId(),
                roomDTO.getHotelId(),
                roomDTO.getNumber(),
                210.0
        );

        RoomDTO actual = given()
                .body(expected)
                .when()
                .put(BASE_URL + "/rooms/{id}", expected.getId())
                .then()
                .statusCode(200)
                .extract()
                .as(RoomDTO.class);

        assertThat(actual, equalTo(expected));
    }

    @Test
    void deleteRoom() {
        RoomDTO roomDTO = roomDTOList.get(0);

        given()
                .when()
                .get(BASE_URL + "/rooms/{id}", roomDTO.getId())
                .then()
                .statusCode(200);

        given()
                .when()
                .delete(BASE_URL + "/rooms/{id}", roomDTO.getId())
                .then()
                .statusCode(204);

        given()
                .when()
                .get(BASE_URL + "/rooms/{id}", roomDTO.getId())
                .then()
                .statusCode(404);
    }
}