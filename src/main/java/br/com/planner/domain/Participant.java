package br.com.planner.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity(name = "participans")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Participant {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String email;

    @Column(name = "is_confirmed", nullable = false)
    private boolean confirmed;

    @ManyToOne
    @JoinColumn(name = "trip_id", insertable = false, updatable = false)
    private Trip trip;

    @Column(name = "trip_id")
    private UUID tripId;
}
