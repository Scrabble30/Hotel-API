package app.security.controllers;

import app.security.enums.Role;
import dk.bugelhartmann.UserDTO;
import io.javalin.http.Context;
import io.javalin.http.UnauthorizedResponse;
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
    public void accessHandler(Context ctx) {

        // If no roles are specified on the endpoint, then anyone can access the route
        if (ctx.routeRoles().isEmpty() || ctx.routeRoles().contains(Role.ANYONE)) {
            return;
        }

        // Check if the user is authenticated
        try {
            securityController.authenticate().handle(ctx);
        } catch (UnauthorizedResponse e) {
            throw new UnauthorizedResponse(e.getMessage());
        } catch (Exception e) {
            throw new UnauthorizedResponse("You need to log in, dude! Or you token is invalid.");
        }

        // Check if the user has the necessary roles to access the route
        UserDTO user = ctx.attribute("user");
        Set<RouteRole> allowedRoles = ctx.routeRoles(); // roles allowed for the current route
        if (!securityController.authorize(user, allowedRoles)) {
            throw new UnauthorizedResponse("Unauthorized with roles: " + (user != null ? user.getRoles() : null) + ". Needed roles are: " + allowedRoles);
        }
    }
}
