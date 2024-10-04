package app.controllers;

import app.daos.IRoomDAO;
import app.dtos.RoomDTO;
import app.exceptions.APIException;
import io.javalin.http.Context;
import jakarta.persistence.EntityNotFoundException;

import java.util.Set;

public class RoomController {

    private static RoomController instance;
    private final IRoomDAO roomDAO;

    private RoomController(IRoomDAO roomDAO) {
        this.roomDAO = roomDAO;
    }

    public static RoomController getInstance(IRoomDAO roomDAO) {
        if (instance == null) {
            instance = new RoomController(roomDAO);
        }

        return instance;
    }

    public void createRoom(Context ctx) {
        try {
            RoomDTO roomDTO = ctx.bodyAsClass(RoomDTO.class);

            RoomDTO createdRoomDTO = roomDAO.createRoom(roomDTO);

            ctx.res().setStatus(201);
            ctx.json(createdRoomDTO, RoomDTO.class);
        } catch (EntityNotFoundException e) {
            throw new APIException(404, e.getMessage());
        } catch (Exception e) {
            throw new APIException(400, e.getMessage());
        }
    }

    public void getRoomById(Context ctx) {
        try {
            Integer id = ctx.pathParamAsClass("id", Integer.class).get();

            RoomDTO roomDTO = roomDAO.getRoomById(id);

            ctx.res().setStatus(200);
            ctx.json(roomDTO, RoomDTO.class);
        } catch (EntityNotFoundException e) {
            throw new APIException(404, e.getMessage());
        } catch (Exception e) {
            throw new APIException(400, e.getMessage());
        }
    }

    public void getAllRooms(Context ctx) {
        try {
            Set<RoomDTO> roomDTOs = roomDAO.getAllRooms();

            ctx.res().setStatus(200);
            ctx.json(roomDTOs, RoomDTO.class);
        } catch (Exception e) {
            throw new APIException(400, e.getMessage());
        }
    }

    public void updateRoom(Context ctx) {
        try {
            Integer id = ctx.pathParamAsClass("id", Integer.class).get();
            RoomDTO roomDTO = ctx.bodyAsClass(RoomDTO.class);
            roomDTO.setId(id);

            RoomDTO updatedRoomDTO = roomDAO.updateRoom(roomDTO);

            ctx.res().setStatus(200);
            ctx.json(updatedRoomDTO, RoomDTO.class);
        } catch (EntityNotFoundException e) {
            throw new APIException(404, e.getMessage());
        } catch (Exception e) {
            throw new APIException(400, e.getMessage());
        }
    }

    public void deleteRoom(Context ctx) {
        try {
            Integer id = ctx.pathParamAsClass("id", Integer.class).get();

            roomDAO.deleteRoom(id);

            ctx.res().setStatus(204);
        } catch (EntityNotFoundException e) {
            throw new APIException(404, e.getMessage());
        } catch (Exception e) {
            throw new APIException(400, e.getMessage());
        }
    }
}
