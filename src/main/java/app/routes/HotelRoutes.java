package app.routes;

import app.controllers.HotelController;
import app.daos.HotelDAO;
import app.daos.IHotelDAO;
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
            post("/", hotelController::createHotel);
            get("/{id}", hotelController::getHotelById);
            get("/{id}/rooms", hotelController::getHotelRoomsById);
            get("/", hotelController::getAllHotels);
            put("/{id}", hotelController::updateHotel);
            delete("/{id}", hotelController::deleteHotel);
        };
    }
}
