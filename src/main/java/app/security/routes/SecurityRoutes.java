package app.security.routes;

import app.security.controllers.SecurityController;
import app.security.enums.Role;
import app.security.utils.Utils;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.javalin.apibuilder.EndpointGroup;
import jakarta.persistence.EntityManagerFactory;

import static io.javalin.apibuilder.ApiBuilder.*;

/**
 * Purpose: To handle security in the API
 * Author: Thomas Hartmann
 */
public class SecurityRoutes {
    private static final ObjectMapper jsonMapper = new Utils().getObjectMapper();
    private static SecurityController securityController;

    public SecurityRoutes(EntityManagerFactory emf) {
        securityController = SecurityController.getInstance(emf);
    }

    public EndpointGroup getSecurityRoutes() {
        return () -> {
            path("/auth", () -> {
                get("/test", ctx -> ctx.json(jsonMapper.createObjectNode().put("msg", "Hello from Open")), Role.ANYONE);
                post("/login", securityController.login(), Role.ANYONE);
                post("/register", securityController.register(), Role.ANYONE);
            });
        };
    }

    public EndpointGroup getSecuredRoutes() {
        return () -> {
            path("/protected", () -> {
                get("/user_demo", (ctx) -> ctx.json(jsonMapper.createObjectNode().put("msg", "Hello from USER Protected")), Role.USER);
                get("/admin_demo", (ctx) -> ctx.json(jsonMapper.createObjectNode().put("msg", "Hello from ADMIN Protected")), Role.ADMIN);
            });
        };
    }
}
