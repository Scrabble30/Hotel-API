package app.daos;

import app.Populator;
import app.config.HibernateConfig;
import app.dtos.HotelDTO;
import app.dtos.RoomDTO;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

class HotelDAOTest {

    private static Populator populator;
    private static IHotelDAO hotelDAO;

    private List<HotelDTO> hotelDTOList;

    @BeforeAll
    static void beforeAll() {
        EntityManagerFactory emf = HibernateConfig.getEntityManagerFactoryForTest();

        populator = new Populator(emf);
        hotelDAO = HotelDAO.getInstance(emf);
    }

    @BeforeEach
    void setUp() {
        hotelDTOList = populator.populateHotelsWithRooms();
    }

    @AfterEach
    void tearDown() {
        populator.cleanUpHotels();
    }

    @Test
    void create() {
        List<RoomDTO> expectedRooms = List.of(
                new RoomDTO(
                        null,
                        null,
                        1,
                        165.0
                )
        );

        HotelDTO expected = new HotelDTO(
                null,
                "Kelp Forest Suites",
                "Clausensvej, 6100 Haderslev",
                Set.of(
                        expectedRooms.get(0)
                )
        );

        HotelDTO actual = hotelDAO.createHotel(expected);
        List<RoomDTO> actualRooms = actual.getRooms().stream().toList();

        expected.setId(actual.getId());

        for (int i = 0; i < expectedRooms.size(); i++) {
            RoomDTO expectedRoom = expectedRooms.get(i);
            RoomDTO actualRoom = actualRooms.get(i);

            expectedRoom.setId(actualRoom.getId());
            expectedRoom.setHotelId(actualRoom.getHotelId());
        }

        Assertions.assertEquals(expected, actual);
        Assertions.assertAll(
                () -> Assertions.assertEquals(expected.getName(), actual.getName()),
                () -> Assertions.assertEquals(expected.getAddress(), actual.getAddress())
        );


    }

    @Test
    void getById() {
        HotelDTO expected = hotelDTOList.get(0);
        HotelDTO actual = hotelDAO.getHotelById(expected.getId());

        Assertions.assertEquals(expected, actual);
    }

    @Test
    void getAll() {
        Set<HotelDTO> expected = new HashSet<>(hotelDTOList);
        Set<HotelDTO> actual = hotelDAO.getAllHotels();

        Assertions.assertEquals(expected, actual);
    }

    @Test
    void update() {
        HotelDTO hotelDTO = hotelDTOList.get(0);
        HotelDTO expected = new HotelDTO(
                hotelDTO.getId(),
                "Renaissance Hotel",
                hotelDTO.getAddress(),
                hotelDTO.getRooms()
        );

        HotelDTO actual = hotelDAO.updateHotel(expected);

        Assertions.assertEquals(expected, actual);
    }

    @Test
    void delete() {
        HotelDTO hotelDTO = hotelDTOList.get(0);

        hotelDAO.deleteHotel(hotelDTO.getId());
        Assertions.assertThrowsExactly(EntityNotFoundException.class, () -> hotelDAO.getHotelById(hotelDTO.getId()));
    }
}