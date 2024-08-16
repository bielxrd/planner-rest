package br.com.planner.mapper;

import br.com.planner.domain.Participant;
import br.com.planner.domain.Trip;
import br.com.planner.dto.trip.TripCreateResponseDTO;
import br.com.planner.dto.trip.TripListPageableResponseDTO;
import br.com.planner.dto.trip.TripResponseDTO;
import br.com.planner.services.participant.ParticipantService;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class TripMapper {

    private ParticipantMapper participantMapper;

    private ParticipantService participantService;

    public TripMapper(ParticipantMapper participantMapper, ParticipantService participantService) {
        this.participantMapper = participantMapper;
        this.participantService = participantService;
    }

    public TripCreateResponseDTO toTripCreateResponseDTO(Trip trip, List<Participant> participants) {
        return TripCreateResponseDTO.builder()
                .tripId(trip.getId())
                .destination(trip.getDestination())
                .participants(participantMapper.toListParticipantResponseDTO(participants))
                .build();
    }

    public TripResponseDTO toTripResponseDTO(Trip trip, List<Participant> participants) {
        return TripResponseDTO.builder()
                .id(trip.getId())
                .destination(trip.getDestination())
                .startsAt(trip.getStartsAt())
                .endsAt(trip.getEndsAt())
                .ownerName(trip.getOwnerName())
                .ownerEmail(trip.getOwnerEmail())
                .confirmed(trip.isConfirmed())
                .participants(participantMapper.toListParticipantResponseDTO(participants))
                .build();
    }

    public TripListPageableResponseDTO toTripListPageableResponseDTO(Page<Trip> trips) {
        List<TripResponseDTO> tripIterator = new ArrayList<>();
        trips.forEach((trip) -> {
            TripResponseDTO tripRequest = TripResponseDTO.builder()
                    .id(trip.getId())
                    .destination(trip.getDestination())
                    .startsAt(trip.getStartsAt())
                    .endsAt(trip.getEndsAt())
                    .ownerName(trip.getOwnerName())
                    .ownerEmail(trip.getOwnerEmail())
                    .participants(participantMapper.toListParticipantResponseDTO(participantService.getParticipants(trip.getId())))
                    .confirmed(trip.isConfirmed())
                    .build();

            tripIterator.add(tripRequest);
        });
        TripListPageableResponseDTO tripsListDTO =  new TripListPageableResponseDTO();
        tripsListDTO.setTrips(tripIterator);
        tripsListDTO.setPageNumber(trips.getNumber());
        tripsListDTO.setPageSize(trips.getSize());
        tripsListDTO.setTotalPages(trips.getTotalPages());

        return tripsListDTO;
    }

}
