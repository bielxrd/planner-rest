package br.com.planner.repositories;

import br.com.planner.domain.Activity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ActivityRepository extends JpaRepository<Activity, UUID> {

    Optional<Activity> findByNameAndTripId(String name, UUID tripId);

}
