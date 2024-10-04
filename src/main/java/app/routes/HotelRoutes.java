package app.routes;

import app.controllers.HotelController;
import app.daos.HotelDAO;
import app.daos.IHotelDAO;
import app.security.enums.Role;
import io.javalin.apibuilder.EndpointGroup;
import jakarta.persistence.EntityManagerFactory;

import static io.javalin.apibuilder.ApiBuilder.*;

public class HotelRoutes {

    private final HotelController hotelController;

    public HotelRoutes(EntityManagerFactory emf) {
        IHotelDAO hotelDAO = HotelDAO.getInstance(emf);

        this.hotelController = HotelController.getInstance(hotelDAO);
    }

    public EndpointGroup getHotelRoutes() {
        return () -> {
            post("/", hotelController::createHotel, Role.ADMIN);
            get("/{id}", hotelController::getHotelById, Role.USER);
            get("/{id}/rooms", hotelController::getHotelRoomsById, Role.USER);
            get("/", hotelController::getAllHotels, Role.USER);
            put("/{id}", hotelController::updateHotel, Role.ADMIN);
            delete("/{id}", hotelController::deleteHotel, Role.ADMIN);
        };
    }
}
