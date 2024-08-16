package br.com.planner.repositories;

import br.com.planner.domain.Trip;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TripRepository extends JpaRepository<Trip, UUID> {
    Page<Trip> findAllByOwnerId(UUID ownerId, Pageable pageable);
    Optional<Trip> findByDestinationContainingIgnoreCaseAndOwnerId(String filter, UUID ownerId);

    @Query(value = "SELECT * FROM trips "+
            "JOIN participants ON trips.id = participants.trip_id "+
            "WHERE participants.owner_id = :ownerId",
            nativeQuery = true)
    Page<Trip> findTripsByParticipantOwnerId(@Param("ownerId") UUID ownerId, Pageable pageable);
}
