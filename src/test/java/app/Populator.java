package app;

import app.dtos.HotelDTO;
import app.entities.Hotel;
import app.entities.Room;
import app.security.entities.Role;
import app.security.entities.User;
import dk.bugelhartmann.UserDTO;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

import java.util.List;

public class Populator {

    private final EntityManagerFactory emf;

    public Populator(EntityManagerFactory emf) {
        this.emf = emf;
    }

    public List<UserDTO> populateUsers() {
        List<User> users = List.of(
                new User(
                        "User1",
                        "user123"
                ),
                new User(
                        "Admin1",
                        "admin123"
                )
        );

        List<Role> roles = List.of(
                new Role(
                        "user"
                ),
                new Role(
                        "admin"
                )
        );

        users.get(0).addRole(roles.get(0));
        roles.get(0).addUser(users.get(0));

        users.get(1).addRole(roles.get(0));
        roles.get(0).addUser(users.get(1));
        users.get(1).addRole(roles.get(1));
        roles.get(1).addUser(users.get(1));

        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            users.forEach(em::persist);
            roles.forEach(em::persist);
            em.getTransaction().commit();

            return users.stream().map(user -> new UserDTO(user.getUsername(), user.getPassword(), user.getRolesAsStrings())).toList();
        }
    }

    public void cleanUpUsers() {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            em.createQuery("DELETE FROM User").executeUpdate();
            em.createQuery("DELETE FROM Role").executeUpdate();
            em.getTransaction().commit();
        }
    }

    public List<HotelDTO> populateHotelsWithRooms() {
        List<Hotel> hotels = List.of(
                Hotel.builder()
                        .name("Azure Skies Resort")
                        .address("Lollandsgade 4-40, 8000 Aarhus")
                        .build(),
                Hotel.builder()
                        .name("Vista Hotel")
                        .address("Plougslundvej, 7190 Billund")
                        .build()
        );

        hotels.get(0).addRoom(
                Room.builder()
                        .number(1)
                        .price(355.0)
                        .build()
        );

        hotels.get(0).addRoom(
                Room.builder()
                        .number(2)
                        .price(180.0)
                        .build()
        );

        hotels.get(1).addRoom(
                Room.builder()
                        .number(1)
                        .price(225.0)
                        .build()
        );

        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            hotels.forEach(hotel -> {
                em.persist(hotel);
                hotel.getRooms().forEach(em::persist);
            });
            em.getTransaction().commit();

            return hotels.stream().map(HotelDTO::new).toList();
        }
    }

    public void cleanUpHotels() {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            em.createQuery("DELETE FROM Room").executeUpdate();
            em.createQuery("DELETE FROM Hotel").executeUpdate();
            em.createNativeQuery("ALTER SEQUENCE room_id_seq RESTART WITH 1;").executeUpdate();
            em.createNativeQuery("ALTER SEQUENCE hotel_id_seq RESTART WITH 1;").executeUpdate();
            em.getTransaction().commit();
        }
    }
}
