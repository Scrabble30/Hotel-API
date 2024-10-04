package app.routes;

import app.controllers.RoomController;
import app.daos.IRoomDAO;
import app.daos.RoomDAO;
import app.security.enums.Role;
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
            post("/", roomController::createRoom, Role.ADMIN);
            get("/{id}", roomController::getRoomById, Role.USER);
            get("/", roomController::getAllRooms, Role.USER);
            put("/{id}", roomController::updateRoom, Role.ADMIN);
            delete("/{id}", roomController::deleteRoom, Role.ADMIN);
        };
    }
}
