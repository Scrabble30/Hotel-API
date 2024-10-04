package app.security.controllers;

import app.exceptions.APIException;
import dk.bugelhartmann.UserDTO;
import io.javalin.http.Context;
import io.javalin.http.HttpStatus;
import io.javalin.security.RouteRole;
import jakarta.persistence.EntityManagerFactory;

import java.util.Set;

public class AccessController implements IAccessController {

    private final SecurityController securityController;

    public AccessController(EntityManagerFactory emf) {
        securityController = SecurityController.getInstance(emf);
    }

    /**
     * This method checks if the user has the necessary roles to access the route.
     *
     * @param ctx
     */
    @Override
    public void accessHandler(Context ctx) {
        UserDTO user = ctx.attribute("user"); // the User was put in the context by the SecurityController.authenticate method (in a before filter on the route)
        Set<RouteRole> allowedRoles = ctx.routeRoles(); // roles allowed for the current route
        if (!securityController.authorize(user, allowedRoles)) {
            if (user != null) {
                throw new APIException(HttpStatus.FORBIDDEN.getCode(), "Unauthorized with roles: " + user.getRoles() + ". Needed roles are: " + allowedRoles);
            } else {
                throw new APIException(HttpStatus.FORBIDDEN.getCode(), "You need to log in, dude!");
            }
        }
    }
}
