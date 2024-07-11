package br.com.planner.strategy.impl;

import br.com.planner.dto.owner.OwnerRequestDTO;
import br.com.planner.exceptions.EmailAlreadyExistsException;
import br.com.planner.repositories.OwnerRepository;
import br.com.planner.strategy.OwnerValidationStrategy;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(1)
public class ExistingEmailValidationImpl implements OwnerValidationStrategy {

    private final OwnerRepository ownerRepository;

    public ExistingEmailValidationImpl(OwnerRepository ownerRepository) {
        this.ownerRepository = ownerRepository;
    }

    @Override
    public void execute(OwnerRequestDTO request) {
        if (isValidEmail(request.getEmail())) {
            throw new EmailAlreadyExistsException("Email already exists");
        }
    }

    private boolean isValidEmail(String email) {
       return ownerRepository.existsByEmail(email);
    }
}
