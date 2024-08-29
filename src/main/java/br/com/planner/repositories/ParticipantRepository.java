package br.com.planner.repositories;

import br.com.planner.domain.Participant;
import br.com.planner.domain.Trip;
import br.com.planner.services.participant.ParticipantService;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ParticipantRepository extends JpaRepository<Participant, UUID> {
    public List<Participant> findAllByTripId(UUID tripId);
    public Optional<Participant> findByTripId(UUID tripId);
    Optional<Participant> findByEmailAndTripId(String email, UUID tripId);
    Participant findByEmail(String email);

    @Query(value = "SELECT p.* FROM participants p WHERE p.email = :email", nativeQuery = true)
    List<Participant> findByEmailQuery(@Param("email") String email);

}
