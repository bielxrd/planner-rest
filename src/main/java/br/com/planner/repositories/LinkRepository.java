package br.com.planner.repositories;

import br.com.planner.domain.Link;
import br.com.planner.domain.Trip;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface LinkRepository extends JpaRepository<Link, UUID> {

    List<Link> findAllByTripId(UUID tripId);

}
