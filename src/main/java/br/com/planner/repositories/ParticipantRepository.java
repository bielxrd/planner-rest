package br.com.planner.repositories;

import br.com.planner.domain.Participant;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ParticipantRepository extends JpaRepository<Participant, UUID> {
    public List<Participant> findAllByTripId(UUID tripId);
    public Optional<Participant> findByTripId(UUID tripId);
}
