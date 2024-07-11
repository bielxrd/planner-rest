package br.com.planner.services;

import br.com.planner.domain.Owner;
import br.com.planner.dto.owner.OwnerRequestDTO;
import br.com.planner.dto.owner.OwnerResponse;
import br.com.planner.repositories.OwnerRepository;
import br.com.planner.strategy.OwnerValidationStrategy;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OwnerService {

    private OwnerRepository ownerRepository;

    private List<OwnerValidationStrategy> ownerValidationStrategy;

    private ModelMapper modelMapper;

    private PasswordEncoder passwordEncoder;

    public OwnerService(OwnerRepository ownerRepository, List<OwnerValidationStrategy> ownerValidationStrategy, ModelMapper modelMapper, PasswordEncoder passwordEncoder) {
        this.ownerRepository = ownerRepository;
        this.ownerValidationStrategy = ownerValidationStrategy;
        this.modelMapper = modelMapper;
        this.passwordEncoder = passwordEncoder;
    }

    public OwnerResponse create(OwnerRequestDTO ownerRequest) {
        ownerValidationStrategy.forEach(validation -> validation.execute(ownerRequest));

        ownerRequest.setPassword(passwordEncoder.encode(ownerRequest.getPassword()));

        Owner ownerMap = modelMapper.map(ownerRequest, Owner.class);

        Owner owner = this.ownerRepository.save(ownerMap);

        return modelMapper.map(owner, OwnerResponse.class);
    }

}
