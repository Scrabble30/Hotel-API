package app.routes;

import app.controllers.RoomController;
import app.daos.IRoomDAO;
import app.daos.RoomDAO;
import io.javalin.apibuilder.EndpointGroup;
import jakarta.persistence.EntityManagerFactory;

import static io.javalin.apibuilder.ApiBuilder.*;

public class RoomRoutes {

    private final RoomController roomController;

    public RoomRoutes(EntityManagerFactory emf) {
        IRoomDAO roomDAO = RoomDAO.getInstance(emf);

        this.roomController = RoomController.getInstance(roomDAO);
    }

    public EndpointGroup getRoomRoutes() {
        return () -> {
            post("/", roomController::createRoom);
            get("/{id}", roomController::getRoomById);
            get("/", roomController::getAllRooms);
            put("/{id}", roomController::updateRoom);
            delete("/{id}", roomController::deleteRoom);
        };
    }
}
