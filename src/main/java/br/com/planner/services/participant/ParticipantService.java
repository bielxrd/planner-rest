package br.com.planner.services.participant;

import br.com.planner.domain.Participant;
import br.com.planner.dto.owner.OwnerRequestDTO;
import br.com.planner.dto.owner.OwnerResponse;
import br.com.planner.dto.participant.ParticipantConfirmRequestDTO;
import br.com.planner.dto.participant.ParticipantResponseDTO;
import br.com.planner.exceptions.AlreadyExistsException;
import br.com.planner.exceptions.NotFoundException;
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

    public List<Participant> getParticipantsWithoutOwnerId(List<Participant> participantsInvited) {
        List<UUID> uuids = participantsInvited.stream()
                .map(Participant::getId)
                .toList();

        List<Participant> invitedParticipantsByUUID = this.participantRepository.findAllById(uuids);

        invitedParticipantsByUUID.removeIf(participant -> participant.getOwnerId() != null);

        return invitedParticipantsByUUID;
    }

    public OwnerResponse assignParticipantToOwner(UUID tripId, OwnerRequestDTO requestDTO) {
        Participant participant = this.participantRepository.findByEmailAndTripId(requestDTO.getEmail(), tripId)
                .orElseThrow(() -> new NotFoundException("You must inform the same email that the owner of the trip informed."));

        if (!participant.getEmail().equalsIgnoreCase(requestDTO.getEmail())) {
            throw new RuntimeException("Email denied.");
        }

        OwnerResponse ownerResponse = this.ownerService.create(requestDTO);

        List<Participant> participants = this.participantRepository.findByEmailQuery(participant.getEmail());

        for (Participant p : participants) {
            p.setName(ownerResponse.getName());
            p.setOwnerId(ownerResponse.getId());
        }

        this.participantRepository.saveAll(participants);

        ownerResponse = OwnerResponse.builder()
                .id(ownerResponse.getId())
                .name(ownerResponse.getName())
                .email(ownerResponse.getEmail())
                .build();

        return ownerResponse;
    }
}
