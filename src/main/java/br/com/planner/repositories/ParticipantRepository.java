package br.com.planner.repositories;

import br.com.planner.domain.Participant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ParticipantRepository extends JpaRepository<Participant, UUID> {
    public List<Participant> findAllByTripId(UUID tripId);
}
