package app.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@NamedQueries(
        @NamedQuery(name = "Hotel.getAll", query = "SELECT h FROM Hotel h")
)
@Entity
@Table(name = "hotel")
public class Hotel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(unique = true, nullable = false)
    private Integer id;
    @Column(nullable = false)
    private String name;
    @Column(nullable = false)
    private String address;
    @Column(nullable = false)
    @OneToMany(mappedBy = "hotel", orphanRemoval = true)
    private Set<Room> rooms;

    @Builder
    public Hotel(String address, Integer id, String name) {
        this.address = address;
        this.id = id;
        this.name = name;
        this.rooms = new HashSet<>();
    }

    public void addRoom(Room room) {
        this.rooms.add(room);
        room.setHotel(this);
    }

    public void removeRoom(Room room) {
        this.rooms.remove(room);
        room.setHotel(null);
    }
}
