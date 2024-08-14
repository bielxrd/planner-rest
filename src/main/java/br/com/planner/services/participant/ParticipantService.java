package br.com.planner.services.participant;

import br.com.planner.domain.Participant;
import br.com.planner.dto.participant.ParticipantConfirmRequestDTO;
import br.com.planner.dto.participant.ParticipantResponseDTO;
import br.com.planner.exceptions.ParticipantAlreadyRegisteredException;
import br.com.planner.exceptions.ParticipantNotFoundException;
import br.com.planner.repositories.ParticipantRepository;
import org.springframework.stereotype.Service;

import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ParticipantService {

    private ParticipantRepository participantRepository;

    public ParticipantService(ParticipantRepository participantRepository) {
        this.participantRepository = participantRepository;
    }

    public List<Participant> registerParticipansToTrip(UUID tripId, List<String> participants) {
        List<Participant> participantsToSave = participants.stream()
                .map(participant -> {

                    Optional<Participant> participantFound = this.participantRepository.findById(tripId);

                    if (participantFound.isPresent()) {
                        throw new ParticipantAlreadyRegisteredException("Participant already registered.");
                    }

                    Participant p = new Participant();
                    p.setEmail(participant);
                    p.setName("");
                    p.setTripId(tripId);
                    p.setConfirmed(false);
                    return p;
                }).toList();

        return this.participantRepository.saveAll(participantsToSave);
    }

    public ParticipantResponseDTO confirmTrip(ParticipantConfirmRequestDTO request, UUID tripId) {
        Participant participant = this.participantRepository.findByTripId(tripId)
                .orElseThrow(() -> new ParticipantNotFoundException("Participant not found."));

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
}
