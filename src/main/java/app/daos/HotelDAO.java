package app.daos;

import app.dtos.HotelDTO;
import app.entities.Hotel;
import jakarta.persistence.*;

import java.util.Set;
import java.util.stream.Collectors;

public class HotelDAO extends AbstractDAO<Hotel> implements IHotelDAO {

    private static HotelDAO instance;

    private HotelDAO(EntityManagerFactory emf) {
        super(Hotel.class, emf);
    }

    public static HotelDAO getInstance(EntityManagerFactory emf) {
        if (instance == null) {
            instance = new HotelDAO(emf);
        }

        return instance;
    }

    @Override
    public HotelDTO createHotel(HotelDTO hotelDTO) {
        try (EntityManager em = emf.createEntityManager()) {
            Hotel hotel = hotelDTO.toEntity();

            em.getTransaction().begin();
            em.persist(hotel);
            hotel.getRooms().forEach(em::persist);
            em.getTransaction().commit();

            return new HotelDTO(hotel);
        } catch (RollbackException e) {
            throw new RollbackException(String.format("Failed to create Hotel: %s", e.getMessage()), e);
        }
    }

    @Override
    public HotelDTO getHotelById(Integer hotelId) {
        try (EntityManager em = emf.createEntityManager()) {
            Hotel foundHotel = em.find(Hotel.class, hotelId);

            if (foundHotel == null) {
                throw new EntityNotFoundException(String.format("Hotel with id %s could not be found.", hotelId));
            }

            return new HotelDTO(foundHotel);
        } catch (RollbackException e) {
            throw new RollbackException(String.format("Failed to get Hotel with id %s", hotelId), e);
        }
    }

    @Override
    public Set<HotelDTO> getAllHotels() {
        try (EntityManager em = emf.createEntityManager()) {
            TypedQuery<Hotel> query = em.createQuery("SELECT e FROM Hotel e", Hotel.class);
            return query.getResultStream().map(HotelDTO::new).collect(Collectors.toSet());
        } catch (RollbackException e) {
            throw new RollbackException("Failed to get all Hotels.", e);
        }
    }

    @Override
    public HotelDTO updateHotel(HotelDTO hotelDTO) {
        try (EntityManager em = emf.createEntityManager()) {
            Hotel foundHotel = em.find(Hotel.class, hotelDTO.getId());

            if (foundHotel == null) {
                throw new EntityNotFoundException(String.format("Hotel with id %d does not exist.", hotelDTO.getId()));
            }

            em.getTransaction().begin();

            if (hotelDTO.getName() != null) {
                foundHotel.setName(hotelDTO.getName());
            }
            if (hotelDTO.getAddress() != null) {
                foundHotel.setAddress(hotelDTO.getAddress());
            }

            em.getTransaction().commit();
            return new HotelDTO(foundHotel);
        } catch (RollbackException e) {
            throw new RollbackException(String.format("Failed to update Hotel: %s", e.getMessage()), e);
        }
    }

    @Override
    public void deleteHotel(Integer hotelId) {
        delete(hotelId);
    }
}
