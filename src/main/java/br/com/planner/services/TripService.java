package br.com.planner.services;

import br.com.planner.domain.Participant;
import br.com.planner.domain.Trip;
import br.com.planner.dto.participant.ParticipantResponseDTO;
import br.com.planner.dto.trip.TripRequestDTO;
import br.com.planner.dto.trip.TripCreateResponseDTO;
import br.com.planner.dto.trip.TripResponseDTO;
import br.com.planner.repositories.TripRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class TripService {

    private TripRepository tripRepository;

    private ParticipantService participantService;

    private ModelMapper modelMapper;

    public TripService(TripRepository tripRepository, ParticipantService participantService, ModelMapper modelMapper) {
        this.tripRepository = tripRepository;
        this.participantService = participantService;
        this.modelMapper = modelMapper;
    }

    public TripCreateResponseDTO create(TripRequestDTO tripRequestDTO) {
        if (tripRequestDTO.getEndsAt().isBefore(tripRequestDTO.getStartsAt())) {
            throw new IllegalArgumentException("End date must be after start date");
        }

        Trip map = modelMapper.map(tripRequestDTO, Trip.class);

        Trip save = this.tripRepository.save(map);

        List<Participant> participants = this.participantService.registerParticipansToTrip(save.getId(), tripRequestDTO.getEmails_to_invite());

        return TripCreateResponseDTO.builder()
                .tripId(save.getId())
                .destination(save.getDestination())
                .participants(mapToParticipantResponse(participants))
                .build();

    }

    public TripResponseDTO getTripById(UUID tripId) {
        Trip trip = this.tripRepository.findById(tripId)
                .orElseThrow(() -> new RuntimeException("Trip not found"));

        List<Participant> participants = this.participantService.getParticipants(tripId);

        TripResponseDTO map = modelMapper.map(trip, TripResponseDTO.class);
        map.setParticipants(mapToParticipantResponse(participants));

        return map;
    }

    private List<ParticipantResponseDTO> mapToParticipantResponse(List<Participant> participants) {
        return participants.stream()
                .map(participant -> modelMapper.map(participant, ParticipantResponseDTO.class))
                .toList();
    }
}
