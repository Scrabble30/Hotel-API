package app.entities;

import jakarta.persistence.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@NamedQueries(
        @NamedQuery(name = "Room.getAll", query = "SELECT r FROM Room r")
)
@Entity
@Table(name = "room")
public class Room {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(unique = true, nullable = false)
    private Integer id;
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @ManyToOne
    @JoinColumn(nullable = false)
    private Hotel hotel;
    @Column(nullable = false)
    private Integer number;
    @Column(nullable = false)
    private Double price;
}
