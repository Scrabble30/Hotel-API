package app.config;

import app.controllers.ExceptionController;
import app.exceptions.APIException;
import app.routes.Routes;
import io.javalin.Javalin;
import io.javalin.config.JavalinConfig;
import jakarta.persistence.EntityManagerFactory;

public class AppConfig {

    private static ExceptionController exceptionController;
    private static Routes routes;

    private static void configuration(JavalinConfig config) {
        config.router.contextPath = "/api/v1";

        config.bundledPlugins.enableRouteOverview("/routes");
        config.bundledPlugins.enableDevLogging();

        config.router.apiBuilder(routes.getAPIRoutes());
    }

    public static void handleExceptions(Javalin app) {
        app.exception(APIException.class, exceptionController::handleAPIExceptions);
    }

    public static Javalin startServer(int port, EntityManagerFactory emf) {
        AppConfig.exceptionController = new ExceptionController();
        AppConfig.routes = new Routes(emf);

        Javalin app = Javalin.create(AppConfig::configuration);
        handleExceptions(app);
        app.start(port);

        return app;
    }

    public static void stopServer(Javalin app) {
        app.stop();
    }
}
