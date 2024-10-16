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
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

class HotelRoutesTest {

    private static final String BASE_URL = "http://localhost:7070/api/v1";

    private static Populator populator;
    private static Javalin app;

    private List<HotelDTO> hotelDTOList;

    @BeforeAll
    static void beforeAll() {
        EntityManagerFactory emf = HibernateConfig.getEntityManagerFactoryForTest();

        populator = new Populator(emf);
        app = AppConfig.startServer(7070, emf);
    }

    @BeforeEach
    void setUp() {
        hotelDTOList = populator.populateHotelsWithRooms();
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
    void createHotel() {
        HotelDTO expected = new HotelDTO(
                null,
                "Kelp Forest Suites",
                "Clausensvej, 6100 Haderslev"
        );

        HotelDTO actual = given()
                .body(expected)
                .when()
                .post(BASE_URL + "/hotels")
                .then()
                .statusCode(201)
                .extract()
                .as(HotelDTO.class);

        expected.setId(actual.getId());

        assertThat(actual, equalTo(expected));
    }

    @Test
    void getHotelById() {
        HotelDTO expected = hotelDTOList.get(0);
        expected.getRooms().clear();

        HotelDTO actual = given()
                .when()
                .get(BASE_URL + "/hotels/{id}", expected.getId())
                .then()
                .statusCode(200)
                .extract()
                .as(HotelDTO.class);

        assertThat(actual, equalTo(expected));
    }

    @Test
    void getHotelRoomsById() {
        HotelDTO expected = hotelDTOList.get(0);
        Set<RoomDTO> expectedRooms = expected.getRooms();

        RoomDTO[] actual = given()
                .when()
                .get(BASE_URL + "/hotels/{id}/rooms", expected.getId())
                .then()
                .statusCode(200)
                .extract()
                .as(RoomDTO[].class);

        Set<RoomDTO> actualRooms = Stream.of(actual).collect(Collectors.toSet());

        assertThat(actualRooms, equalTo(expectedRooms));
    }

    @Test
    void getAllHotels() {
        List<HotelDTO> expectedHotelDTOList = hotelDTOList;
        expectedHotelDTOList.forEach(hotelDTO -> hotelDTO.getRooms().clear());

        HotelDTO[] actual = given()
                .when()
                .get(BASE_URL + "/hotels")
                .then()
                .statusCode(200)
                .extract()
                .as(HotelDTO[].class);

        List<HotelDTO> actualHotelDTOList = Stream.of(actual).toList();

        assertThat(actualHotelDTOList, equalTo(expectedHotelDTOList));
    }

    @Test
    void updateHotel() {
        HotelDTO hotelDTO = hotelDTOList.get(0);
        HotelDTO expected = new HotelDTO(
                hotelDTO.getId(),
                String.format("Updated %s", hotelDTO.getName()),
                hotelDTO.getAddress()
        );

        HotelDTO actual = given()
                .body(expected)
                .when()
                .put(BASE_URL + "/hotels/{id}", expected.getId())
                .then()
                .statusCode(200)
                .extract()
                .as(HotelDTO.class);

        assertThat(actual, equalTo(expected));
    }

    @Test
    void deleteHotel() {
        HotelDTO hotelDTO = hotelDTOList.get(0);

        given()
                .when()
                .get(BASE_URL + "/hotels/{id}", hotelDTO.getId())
                .then()
                .statusCode(200);

        given()
                .when()
                .delete(BASE_URL + "/hotels/{id}", hotelDTO.getId())
                .then()
                .statusCode(204);

        given()
                .when()
                .get(BASE_URL + "/hotels/{id}", hotelDTO.getId())
                .then()
                .statusCode(404);
    }
}