package app.dtos;

import app.entities.Hotel;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonPropertyOrder({"id", "name", "address"})
public class HotelDTO {

    private Integer id;
    private String name;
    private String address;
    @JsonIgnore
    private Set<RoomDTO> rooms;

    @JsonCreator
    public HotelDTO(
            @JsonProperty("id") Integer id,
            @JsonProperty("name") String name,
            @JsonProperty("address") String address) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.rooms = new HashSet<>();
    }

    public HotelDTO(Hotel hotel) {
        this.id = hotel.getId();
        this.name = hotel.getName();
        this.address = hotel.getAddress();
        this.rooms = hotel.getRooms().stream().map(RoomDTO::new).collect(Collectors.toSet());
    }

    @JsonIgnore
    public Hotel toEntity() {
        Hotel hotel = Hotel.builder()
                .id(id)
                .name(name)
                .address(address)
                .build();

        rooms.stream().map(RoomDTO::toEntity).forEach(hotel::addRoom);

        return hotel;
    }
}
