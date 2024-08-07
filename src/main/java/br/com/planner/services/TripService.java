package br.com.planner.services;

import br.com.planner.domain.Owner;
import br.com.planner.domain.Participant;
import br.com.planner.domain.Trip;
import br.com.planner.dto.participant.ParticipantResponseDTO;
import br.com.planner.dto.trip.*;
import br.com.planner.exceptions.OwnerNotFoundException;
import br.com.planner.exceptions.TripAlreadyConfirmedException;
import br.com.planner.exceptions.TripDateException;
import br.com.planner.exceptions.TripNotFoundException;
import br.com.planner.repositories.OwnerRepository;
import br.com.planner.repositories.TripRepository;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class TripService {

    private TripRepository tripRepository;

    private ParticipantService participantService;

    private OwnerRepository ownerRepository;

    private ModelMapper modelMapper;

    public TripService(TripRepository tripRepository, ParticipantService participantService, ModelMapper modelMapper, OwnerRepository ownerRepository) {
        this.tripRepository = tripRepository;
        this.participantService = participantService;
        this.modelMapper = modelMapper;
        this.ownerRepository = ownerRepository;
    }

    public TripCreateResponseDTO create(TripRequestDTO tripRequestDTO, UUID ownerId) {
        tripDateValidation(tripRequestDTO.getStartsAt(), tripRequestDTO.getEndsAt());

        Owner owner = this.ownerRepository.findById(ownerId)
                .orElseThrow(() -> new OwnerNotFoundException("Owner not found"));

        Trip map = modelMapper.map(tripRequestDTO, Trip.class);
        map.setOwnerName(owner.getName());
        map.setOwnerEmail(owner.getEmail());
        map.setOwnerId(ownerId);

        Trip save = this.tripRepository.save(map);

        List<Participant> participants = this.participantService.registerParticipansToTrip(save.getId(), tripRequestDTO.getEmails_to_invite());

        return TripCreateResponseDTO.builder()
                .tripId(save.getId())
                .destination(save.getDestination())
                .participants(mapToParticipantResponse(participants))
                .build();

    }

    public TripListPageableResponseDTO getAllTrips(int pageNumber, int pageSize, UUID ownerId) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        Page<Trip> tripsPageable = this.tripRepository.findAllByOwnerId(ownerId, pageable);
        List<TripResponseDTO> tripIterator = new ArrayList<>();
        tripsPageable.forEach((trip) -> {
            TripResponseDTO tripRequest = TripResponseDTO.builder()
                    .destination(trip.getDestination())
                    .startsAt(trip.getStartsAt())
                    .endsAt(trip.getEndsAt())
                    .ownerName(trip.getOwnerName())
                    .ownerEmail(trip.getOwnerEmail())
                    .participants(mapToParticipantResponse(participantService.getParticipants(trip.getId())))
                    .confirmed(trip.isConfirmed())
                    .build();

            tripIterator.add(tripRequest);
        });
        TripListPageableResponseDTO trips =  new TripListPageableResponseDTO();
        trips.setTrips(tripIterator);
        trips.setPageNumber(tripsPageable.getNumber());
        trips.setPageSize(tripsPageable.getSize());
        trips.setTotalPages(tripsPageable.getTotalPages());

        return trips;
    }

    public TripResponseDTO getTripById(UUID tripId) {
        Trip trip = this.tripRepository.findById(tripId)
                .orElseThrow(() -> new TripNotFoundException("Trip not found"));

        List<Participant> participants = this.participantService.getParticipants(tripId);


        return TripResponseDTO.builder()
                .destination(trip.getDestination())
                .startsAt(trip.getStartsAt())
                .endsAt(trip.getEndsAt())
                .ownerName(trip.getOwnerName())
                .ownerEmail(trip.getOwnerEmail())
                .confirmed(trip.isConfirmed())
                .participants(mapToParticipantResponse(participants))
                .build();
    }

    public TripResponseDTO findTripByDestinationFilter(String destination, UUID ownerId) {
        Trip trip = this.tripRepository.findByDestinationContainingIgnoreCaseAndOwnerId(destination, ownerId)
                .orElseThrow(() -> new TripNotFoundException("Trip not found"));

        List<Participant> participants = this.participantService.getParticipants(trip.getId());

        return TripResponseDTO.builder()
                .destination(trip.getDestination())
                .startsAt(trip.getStartsAt())
                .endsAt(trip.getEndsAt())
                .ownerName(trip.getOwnerName())
                .ownerEmail(trip.getOwnerEmail())
                .confirmed(trip.isConfirmed())
                .participants(mapToParticipantResponse(participants))
                .build();
    }

    public UpdateTripDTO updateTrip(UUID tripId, UpdateTripDTO request) {

        tripDateValidation(request.getStartsAt(), request.getEndsAt());

        Trip tripRequest = this.tripRepository.findById(tripId)
                .orElseThrow(() -> new TripNotFoundException("Trip not found"));

        tripRequest.setDestination(request.getDestination());
        tripRequest.setStartsAt(request.getStartsAt());
        tripRequest.setEndsAt(request.getEndsAt());

        Trip trip = this.tripRepository.save(tripRequest);

        return modelMapper.map(trip, UpdateTripDTO.class);
    }

    public TripIdDto confirmTrip(UUID tripId) {
        Trip trip = this.tripRepository.findById(tripId)
                .orElseThrow(() -> new TripNotFoundException("Trip not found"));

        if (trip.isConfirmed()) {
            throw new TripAlreadyConfirmedException("Trip has already been confirmed.");
        }

        trip.setConfirmed(true);
        Trip updatedTrip = this.tripRepository.save(trip);

        return new TripIdDto(updatedTrip.getId());

    }

    public void deleteTripById(UUID tripId) {
        if (!tripRepository.existsById(tripId)) {
            throw new TripNotFoundException("Trip not found");
        }

        this.tripRepository.deleteById(tripId);
    }

    public TripResponseDTO sendInvites(TripInviteDTO request, UUID tripId) {
        Trip trip = this.tripRepository.findById(tripId)
                .orElseThrow(() -> new TripNotFoundException("Trip not found"));

        List<Participant> participants = this.participantService.registerParticipansToTrip(tripId, request.getEmailsToInvite());

        return TripResponseDTO.builder()
                .destination(trip.getDestination())
                .startsAt(trip.getStartsAt())
                .endsAt(trip.getEndsAt())
                .ownerName(trip.getOwnerName())
                .ownerEmail(trip.getOwnerEmail())
                .confirmed(trip.isConfirmed())
                .participants(mapToParticipantResponse(participants))
                .build();
    }

    private List<ParticipantResponseDTO> mapToParticipantResponse(List<Participant> participants) {
        return participants.stream()
                .map(participant -> modelMapper.map(participant, ParticipantResponseDTO.class))
                .toList();
    }

    private void tripDateValidation(LocalDateTime startsAt, LocalDateTime endsAt) {
        if (endsAt.isBefore(startsAt)) {
            throw new TripDateException("End date must be after start date.");
        }
    }
}
