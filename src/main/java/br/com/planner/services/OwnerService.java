package br.com.planner.services;

import br.com.planner.domain.Owner;
import br.com.planner.dto.owner.AuthOwnerRequestDTO;
import br.com.planner.dto.owner.AuthOwnerResponseDTO;
import br.com.planner.dto.owner.OwnerRequestDTO;
import br.com.planner.dto.owner.OwnerResponse;
import br.com.planner.exceptions.EmailNotFoundException;
import br.com.planner.exceptions.EmailOrPasswordWrongException;
import br.com.planner.repositories.OwnerRepository;
import br.com.planner.strategy.OwnerValidationStrategy;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.lang.reflect.Array;
import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;

@Service
public class OwnerService {

    private OwnerRepository ownerRepository;

    private List<OwnerValidationStrategy> ownerValidationStrategy;

    private ModelMapper modelMapper;

    private PasswordEncoder passwordEncoder;

    @Value("${owner.security.token}")
    private String secretKey;

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

    public AuthOwnerResponseDTO auth(AuthOwnerRequestDTO authOwnerRequestDTO) {
        Owner owner = this.ownerRepository.findByEmail(authOwnerRequestDTO.getEmail())
                .orElseThrow(() -> new EmailNotFoundException("Email/Password incorrect."));

        boolean matches = passwordEncoder.matches(authOwnerRequestDTO.getPassword(), owner.getPassword());

        if (!matches) {
            throw new EmailOrPasswordWrongException("Email/password incorrect.");
        }

        Algorithm algorithm = Algorithm.HMAC256(secretKey);
        Instant expiresIn = Instant.now().plus(Duration.ofHours(2));
        String token = JWT.create().withIssuer("OWNER")
                .withExpiresAt(expiresIn)
                .withSubject(owner.getId().toString())
                .sign(algorithm);

        return AuthOwnerResponseDTO.builder()
                .token(token)
                .owner(new OwnerResponse(owner.getName(), owner.getEmail(), owner.getTrips()))
                .build();

    }

}




