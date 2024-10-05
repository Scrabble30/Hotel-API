package app.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class HttpMessageDTO {

    private int status;
    private String message;
}
