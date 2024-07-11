package br.com.planner.strategy.impl;

import br.com.planner.dto.owner.OwnerRequestDTO;
import br.com.planner.exceptions.PasswordRegexException;
import br.com.planner.strategy.OwnerValidationStrategy;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(3)
public class PasswordRequirementsValidationImpl implements OwnerValidationStrategy {
    @Override
    public void execute(OwnerRequestDTO request) {
        if(!request.getPassword().matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[\\W_]).+$")){
            throw new PasswordRegexException("A senha deve ter pelo menos: 1 caractere minusculo, 1 caractere maiusculo, 1 numero e 1 caractere especial");
        }
    }
}
