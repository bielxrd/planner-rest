package br.com.planner.strategy;

import br.com.planner.dto.owner.OwnerRequestDTO;

public interface OwnerValidationStrategy {
    void execute(OwnerRequestDTO request);
}
