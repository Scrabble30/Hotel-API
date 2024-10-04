package app.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class APIMessageDTO {

    private int status;
    private String message;
}
