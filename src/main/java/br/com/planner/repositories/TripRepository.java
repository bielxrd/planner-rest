package br.com.planner.repositories;

import br.com.planner.domain.Trip;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface TripRepository extends JpaRepository<Trip, UUID> {
    Page<Trip> findAllByOwnerId(UUID ownerId, Pageable pageable);
}
