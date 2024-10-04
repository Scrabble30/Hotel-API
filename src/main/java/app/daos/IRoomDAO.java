package app.daos;

import app.dtos.RoomDTO;

import java.util.Set;

public interface IRoomDAO {

    RoomDTO createRoom(RoomDTO roomDTO);

    RoomDTO getRoomById(Integer id);

    Set<RoomDTO> getAllRooms();

    RoomDTO updateRoom(RoomDTO roomDTO);

    void deleteRoom(Integer id);
}
