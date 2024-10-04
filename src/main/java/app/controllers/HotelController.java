package app.controllers;

import app.daos.IHotelDAO;
import app.dtos.HotelDTO;
import app.dtos.RoomDTO;
import app.exceptions.APIException;
import io.javalin.http.Context;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.geom.IllegalPathStateException;
import java.util.Set;

public class HotelController {

    private static final Logger logger = LoggerFactory.getLogger(HotelController.class);

    private static HotelController instance;
    private final IHotelDAO hotelDAO;

    private HotelController(IHotelDAO hotelDAO) {
        this.hotelDAO = hotelDAO;
    }

    public static HotelController getInstance(IHotelDAO hotelDAO) {
        if (instance == null) {
            instance = new HotelController(hotelDAO);
        }

        return instance;
    }

    public void createHotel(Context ctx) {
        try {
            HotelDTO hotelDTO = ctx.bodyAsClass(HotelDTO.class);
            HotelDTO createdHotelDTO = hotelDAO.createHotel(hotelDTO);

            ctx.res().setStatus(201);
            ctx.json(createdHotelDTO, HotelDTO.class);
        } catch (Exception e) {
            throw new APIException(400, e.getMessage());
        }
    }

    public void getHotelById(Context ctx) {
        try {
            logger.info("Request: {} {} {}", ctx.method(), ctx.path(), ctx.body());

            Integer id = ctx.pathParamAsClass("id", Integer.class).getOrThrow(errors -> {
                Object value = errors.get("id").get(0).getValue();
                return new IllegalPathStateException(String.format("The value %s is not a valid hotel id.", value));
            });

            HotelDTO hotelDTO = hotelDAO.getHotelById(id);

            ctx.res().setStatus(200);
            ctx.json(hotelDTO, HotelDTO.class);

            logger.info("Response: {} {}", ctx.res().getStatus(), hotelDTO);
        } catch (EntityNotFoundException e) {
            throw new APIException(404, e.getMessage());
        } catch (Exception e) {
            throw new APIException(400, e.getMessage());
        }
    }

    public void getHotelRoomsById(Context ctx) {
        try {
            Integer id = ctx.pathParamAsClass("id", Integer.class).getOrThrow(errors -> {
                Object value = errors.get("id").get(0).getValue();
                return new IllegalPathStateException(String.format("The value %s is not a valid hotel id.", value));
            });

            HotelDTO hotelDTO = hotelDAO.getHotelById(id);

            ctx.res().setStatus(200);
            ctx.json(hotelDTO.getRooms(), RoomDTO.class);
        } catch (EntityNotFoundException e) {
            throw new APIException(404, e.getMessage());
        } catch (Exception e) {
            throw new APIException(400, e.getMessage());
        }
    }

    public void getAllHotels(Context ctx) {
        try {
            Set<HotelDTO> hotelDTOSet = hotelDAO.getAllHotels();

            ctx.res().setStatus(200);
            ctx.json(hotelDTOSet, HotelDTO.class);
        } catch (Exception e) {
            throw new APIException(500, e.getMessage());
        }
    }

    public void updateHotel(Context ctx) {
        try {
            Integer id = ctx.pathParamAsClass("id", Integer.class).get();
            HotelDTO hotelDTO = ctx.bodyAsClass(HotelDTO.class);
            hotelDTO.setId(id);

            HotelDTO updatedHotelDTO = hotelDAO.updateHotel(hotelDTO);

            ctx.res().setStatus(200);
            ctx.json(updatedHotelDTO, HotelDTO.class);
        } catch (EntityNotFoundException e) {
            throw new APIException(404, e.getMessage());
        } catch (Exception e) {
            throw new APIException(400, e.getMessage());
        }
    }

    public void deleteHotel(Context ctx) {
        try {
            Integer id = ctx.pathParamAsClass("id", Integer.class).get();

            hotelDAO.deleteHotel(id);

            ctx.res().setStatus(204);
        } catch (EntityNotFoundException e) {
            throw new APIException(404, e.getMessage());
        } catch (Exception e) {
            throw new APIException(400, e.getMessage());
        }
    }
}
