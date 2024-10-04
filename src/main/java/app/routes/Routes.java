package app.routes;

import io.javalin.apibuilder.EndpointGroup;
import jakarta.persistence.EntityManagerFactory;

import static io.javalin.apibuilder.ApiBuilder.path;

public class Routes {

    private final HotelRoutes hotelRoutes;
    private final RoomRoutes roomRoutes;

    public Routes(EntityManagerFactory emf) {
        this.hotelRoutes = new HotelRoutes(emf);
        this.roomRoutes = new RoomRoutes(emf);
    }

    public EndpointGroup getAPIRoutes() {
        return () -> {
            path("/hotels", hotelRoutes.getHotelRoutes());
            path("/rooms", roomRoutes.getRoomRoutes());
        };
    }
}
