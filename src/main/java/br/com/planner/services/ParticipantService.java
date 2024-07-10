package br.com.planner.services;

import br.com.planner.domain.Participant;
import br.com.planner.repositories.ParticipantRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class ParticipantService {

    private ParticipantRepository participantRepository;

    public ParticipantService(ParticipantRepository participantRepository) {
        this.participantRepository = participantRepository;
    }

    public List<Participant> registerParticipansToTrip(UUID tripId, List<String> participants) {
        List<Participant> participantsToSave = participants.stream()
                .map(participant -> {
                    Participant p = new Participant();
                    p.setEmail(participant);
                    p.setName("");
                    p.setTripId(tripId);
                    p.setConfirmed(false);
                    return p;
                }).toList();

        return this.participantRepository.saveAll(participantsToSave);
    }

    public List<Participant> getParticipants(UUID tripId) {
        return participantRepository.findAllByTripId(tripId);
    }
}
