package app.dtos;

import app.entities.Room;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoomDTO {

    private Integer id;
    private Integer hotelId;
    private Integer number;
    private Double price;

    public RoomDTO(Room room) {
        this.id = room.getId();
        this.hotelId = room.getHotel().getId();
        this.number = room.getNumber();
        this.price = room.getPrice();
    }

    @JsonIgnore
    public Room toEntity() {
        return Room.builder()
                .id(id)
                .number(number)
                .price(price)
                .build();
    }
}
