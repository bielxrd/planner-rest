package br.com.planner.strategy.impl;

import br.com.planner.dto.owner.OwnerRequestDTO;
import br.com.planner.exceptions.PasswordLengthException;
import br.com.planner.strategy.OwnerValidationStrategy;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(2)
public class PasswordLengthValidationImpl implements OwnerValidationStrategy {

    @Override
    public void execute(OwnerRequestDTO request) {
        if (request.getPassword().length() < 8 || request.getPassword().length() > 16) {
            throw new PasswordLengthException("Password must be between 8 and 16 characters");
        }
    }
}
