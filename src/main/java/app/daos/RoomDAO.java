package app.daos;

import app.dtos.RoomDTO;
import app.entities.Hotel;
import app.entities.Room;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.RollbackException;

import java.util.Set;
import java.util.stream.Collectors;

public class RoomDAO extends AbstractDAO<Room> implements IRoomDAO {

    private static RoomDAO roomDAO;

    private RoomDAO(EntityManagerFactory emf) {
        super(Room.class, emf);
    }

    public static RoomDAO getInstance(EntityManagerFactory emf) {
        if (roomDAO == null) {
            roomDAO = new RoomDAO(emf);
        }

        return roomDAO;
    }

    @Override
    public RoomDTO createRoom(RoomDTO roomDTO) {
        try (EntityManager em = emf.createEntityManager()) {
            Room room = roomDTO.toEntity();

            Hotel foundHotel = em.find(Hotel.class, roomDTO.getHotelId());

            if (foundHotel != null) {
                room.setHotel(foundHotel);
            } else {
                throw new EntityNotFoundException(String.format("Hotel with id %d could not be found.", roomDTO.getHotelId()));
            }

            em.getTransaction().begin();
            em.persist(room);
            em.getTransaction().commit();

            return new RoomDTO(room);
        } catch (RollbackException e) {
            throw new RollbackException(String.format("Failed to create Room: %s", e.getMessage()), e);
        }
    }

    @Override
    public RoomDTO getRoomById(Integer id) {
        return new RoomDTO(getById(id));
    }

    @Override
    public Set<RoomDTO> getAllRooms() {
        return getAll().stream().map(RoomDTO::new).collect(Collectors.toSet());
    }

    @Override
    public RoomDTO updateRoom(RoomDTO roomDTO) {
        try (EntityManager em = emf.createEntityManager()) {
            Room foundRoom = em.find(Room.class, roomDTO.getId());

            if (foundRoom == null) {
                throw new EntityNotFoundException(String.format("Room with id %d could not be found.", roomDTO.getId()));
            }

            em.getTransaction().begin();

            if (roomDTO.getHotelId() != null) {
                Hotel foundHotel = em.find(Hotel.class, roomDTO.getHotelId());

                if (foundHotel != null) {
                    foundRoom.setHotel(foundHotel);
                } else {
                    throw new EntityNotFoundException(String.format("Hotel with id %d does not exist.", roomDTO.getHotelId()));
                }
            }
            if (roomDTO.getNumber() != null) {
                foundRoom.setNumber(roomDTO.getNumber());
            }
            if (roomDTO.getPrice() != null) {
                foundRoom.setPrice(roomDTO.getPrice());
            }

            em.getTransaction().commit();
            return new RoomDTO(foundRoom);
        } catch (RollbackException e) {
            throw new RollbackException(String.format("Failed to update Room: %s", e.getMessage()), e);
        }
    }

    @Override
    public void deleteRoom(Integer id) {
        delete(id);
    }
}
