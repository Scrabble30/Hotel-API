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
import java.util.stream.Collectors;

class RoomDAOTest {

    private static Populator populator;
    private static IRoomDAO roomDAO;

    private List<HotelDTO> hotelDTOList;
    private List<RoomDTO> roomDTOList;

    @BeforeAll
    static void beforeAll() {
        EntityManagerFactory emf = HibernateConfig.getEntityManagerFactoryForTest();

        populator = new Populator(emf);
        roomDAO = RoomDAO.getInstance(emf);
    }

    @BeforeEach
    void setUp() {
        hotelDTOList = populator.populateHotelsWithRooms();
        roomDTOList = hotelDTOList.stream().map(HotelDTO::getRooms).flatMap(Set::stream).collect(Collectors.toList());
    }

    @AfterEach
    void tearDown() {
        populator.cleanUpHotels();
    }

    @Test
    void create() {
        RoomDTO expected = new RoomDTO(
                null,
                hotelDTOList.get(1).getId(),
                2,
                230.0
        );

        RoomDTO actual = roomDAO.createRoom(expected);
        expected.setId(actual.getId());

        Assertions.assertEquals(expected, actual);
    }

    @Test
    void getById() {
        RoomDTO expected = roomDTOList.get(0);
        RoomDTO actual = roomDAO.getRoomById(expected.getId());

        Assertions.assertEquals(expected, actual);
    }

    @Test
    void getAll() {
        Set<RoomDTO> expected = new HashSet<>(roomDTOList);
        Set<RoomDTO> actual = roomDAO.getAllRooms();

        Assertions.assertEquals(expected, actual);
    }

    @Test
    void update() {
        RoomDTO roomDTO = roomDTOList.get(0);
        RoomDTO expected = new RoomDTO(
                roomDTO.getId(),
                roomDTO.getHotelId(),
                3,
                450.0
        );

        RoomDTO actual = roomDAO.updateRoom(expected);

        Assertions.assertEquals(expected, actual);
    }

    @Test
    void delete() {
        RoomDTO roomDTO = roomDTOList.get(0);

        roomDAO.deleteRoom(roomDTO.getId());
        Assertions.assertThrowsExactly(EntityNotFoundException.class, () -> roomDAO.getRoomById(roomDTO.getId()));
    }
}