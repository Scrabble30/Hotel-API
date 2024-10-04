package app.daos;

import app.dtos.HotelDTO;

import java.util.Set;

public interface IHotelDAO {

    HotelDTO createHotel(HotelDTO hotelDTO);

    HotelDTO getHotelById(Integer hotelId);

    Set<HotelDTO> getAllHotels();

    HotelDTO updateHotel(HotelDTO hotelDTO);

    void deleteHotel(Integer hotelId);
}
