package app.controllers;

import app.dtos.APIMessageDTO;
import app.exceptions.APIException;
import io.javalin.http.Context;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExceptionController {

    private final Logger logger = LoggerFactory.getLogger(ExceptionController.class);

    public void handleAPIExceptions(APIException e, Context ctx) {
        logger.error("{} {}", e.getStatusCode(), e.getMessage());

        ctx.status(e.getStatusCode());
        ctx.json(new APIMessageDTO(e.getStatusCode(), e.getMessage()));
    }
}
