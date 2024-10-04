package app.exceptions;

import lombok.Getter;

@Getter
public class APIException extends RuntimeException {

    private final int statusCode;

    public APIException(int statusCode, String message) {
        super(message);

        this.statusCode = statusCode;
    }
}
