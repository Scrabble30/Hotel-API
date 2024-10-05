package app.config;

import app.controllers.ExceptionController;
import app.dtos.HttpMessageDTO;
import app.exceptions.APIException;
import app.routes.Routes;
import app.security.controllers.AccessController;
import app.security.routes.SecurityRoutes;
import io.javalin.Javalin;
import io.javalin.config.JavalinConfig;
import jakarta.persistence.EntityManagerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AppConfig {

    private static final Logger logger = LoggerFactory.getLogger(ExceptionController.class);

    private static ExceptionController exceptionController;
    private static AccessController accessController;

    private static SecurityRoutes securityRoutes;
    private static Routes routes;

    private static void configuration(JavalinConfig config) {
        config.router.contextPath = "/api/v1";

        config.bundledPlugins.enableRouteOverview("/routes");
        config.bundledPlugins.enableDevLogging();

        config.router.apiBuilder(routes.getAPIRoutes());
        config.router.apiBuilder(securityRoutes.getSecuredRoutes());
        config.router.apiBuilder(securityRoutes.getSecurityRoutes());
    }

    public static void handleExceptions(Javalin app) {
        app.exception(APIException.class, exceptionController::handleAPIExceptions);
        app.exception(Exception.class, (e, ctx) -> {
            logger.error("{} {}", 400, e.getMessage());

            ctx.status(400);
            ctx.json(new HttpMessageDTO(400, e.getMessage()));
        });
    }

    public static Javalin startServer(int port, EntityManagerFactory emf) {
        AppConfig.exceptionController = new ExceptionController();
        AppConfig.accessController = new AccessController(emf);

        AppConfig.securityRoutes = new SecurityRoutes(emf);
        AppConfig.routes = new Routes(emf);

        Javalin app = Javalin.create(AppConfig::configuration);
        app.beforeMatched(accessController::accessHandler);
        handleExceptions(app);
        app.start(port);

        return app;
    }

    public static void stopServer(Javalin app) {
        app.stop();
    }
}
