package br.com.planner.repositories;

import br.com.planner.domain.Owner;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface OwnerRepository extends JpaRepository<Owner, UUID> {
    boolean existsByEmail(String email);
}
