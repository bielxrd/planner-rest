package br.com.planner.services.participant;

import br.com.planner.domain.Owner;
import br.com.planner.domain.Participant;
import br.com.planner.dto.owner.*;
import br.com.planner.dto.participant.ParticipantConfirmRequestDTO;
import br.com.planner.dto.participant.ParticipantResponseDTO;
import br.com.planner.exceptions.AlreadyExistsException;
import br.com.planner.exceptions.NotFoundException;
import br.com.planner.repositories.OwnerRepository;
import br.com.planner.repositories.ParticipantRepository;
import br.com.planner.services.owner.OwnerService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ParticipantService {

    private ParticipantRepository participantRepository;

    private OwnerService ownerService;

    private OwnerRepository ownerRepository;

    public ParticipantService(ParticipantRepository participantRepository, OwnerService ownerService) {
        this.participantRepository = participantRepository;
        this.ownerService = ownerService;
    }

    public List<Participant> registerParticipansToTrip(UUID tripId, List<String> participants) {
        List<Participant> participantsToSave = participants.stream()
                .map(participant -> {

                    Optional<Participant> participantFound = this.participantRepository.findByTripId(tripId);

                    if (participantFound.isPresent()) {
                        throw new AlreadyExistsException("Participant already registered.");
                    }

//                    Participant participantByEmail = this.participantRepository.findByEmail(participant);

                    Participant p = new Participant();
                    p.setEmail(participant);
                    p.setName("");
                    p.setTripId(tripId);
                    p.setConfirmed(false);

//                    if (participantByEmail != null && participantByEmail.getOwnerId() != null) {
//                        p.setOwnerId(participantByEmail.getOwnerId());
//                    }

                    return p;
                }).toList();

        return this.participantRepository.saveAll(participantsToSave);
    }

    public ParticipantResponseDTO confirmTrip(ParticipantConfirmRequestDTO request, UUID tripId) {
        Participant participant = this.participantRepository.findByTripId(tripId)
                .orElseThrow(() -> new NotFoundException("Participant not found."));

        participant.setName(request.getName());
        participant.setConfirmed(true);

        this.participantRepository.save(participant);

        return ParticipantResponseDTO.builder()
                .name(participant.getName())
                .email(participant.getEmail())
                .confirmed(participant.isConfirmed())
                .build();
    }

    public List<Participant> getParticipants(UUID tripId) {
        return participantRepository.findAllByTripId(tripId);
    }

    public List<Participant> getParticipantsWithOwnerId(UUID tripId) {
        List<Participant> participants = getParticipants(tripId);

        participants.removeIf(participant -> participant.getOwnerId() == null);

        return participants;
    }

    public List<Participant> getParticipantsWithoutOwnerId(UUID tripId) {
        List<Participant> participants = getParticipants(tripId);

        participants.removeIf(participant -> participant.getOwnerId() != null);

        return participants;
    }

    public VerifyOwnerResponseDTO verifyOwnerExistingAccount(String email) {
        Optional<Owner> owner = this.ownerRepository.findByEmail(email);

        if (owner.isPresent()) {
            return VerifyOwnerResponseDTO.builder()
                    .owner_exists(true)
                    .ownerId(owner.get().getId())
                    .build();
        }

        return VerifyOwnerResponseDTO.builder()
                .owner_exists(false)
                .ownerId(null)
                .build();
    }

    public OwnerResponse assignParticipantToOwnerAlreadyCreated(UUID tripId, AssignOwnerRequest ownerRequest) {
        Participant participant = this.participantRepository.findByEmailAndTripId(ownerRequest.getEmail(), tripId)
                .orElseThrow(() -> new NotFoundException("Participant not found, cannot assign"));

        Owner owner = this.ownerRepository.findById(ownerRequest.getOwnerId())
                .orElseThrow(() -> new NotFoundException("Owner not found"));

        participant.setName(owner.getName());
        participant.setOwnerId(ownerRequest.getOwnerId());

        this.participantRepository.save(participant);

        return OwnerResponse.builder()
                .id(owner.getId())
                .name(participant.getName())
                .email(participant.getEmail())
                .build();
    }

    public OwnerResponse assignParticipantToOwner(UUID tripId, OwnerRequestDTO requestDTO) {
        Participant participant = this.participantRepository.findByEmailAndTripId(requestDTO.getEmail(), tripId)
                .orElseThrow(() -> new NotFoundException("You must inform the same email that the owner of the trip informed."));

        if (!participant.getEmail().equalsIgnoreCase(requestDTO.getEmail())) {
            throw new RuntimeException("Email denied.");
        }

        OwnerResponse ownerResponse = this.ownerService.create(requestDTO);

        participant.setName(ownerResponse.getName());
        participant.setOwnerId(ownerResponse.getId());

        this.participantRepository.save(participant);

        ownerResponse = OwnerResponse.builder()
                .id(ownerResponse.getId())
                .name(ownerResponse.getName())
                .email(ownerResponse.getEmail())
                .build();

        return ownerResponse;
    }
}
