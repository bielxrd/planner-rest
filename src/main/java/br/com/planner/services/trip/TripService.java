package br.com.planner.services.trip;

import br.com.planner.domain.Owner;
import br.com.planner.domain.Participant;
import br.com.planner.domain.Trip;
import br.com.planner.dto.email.Email;
import br.com.planner.dto.trip.*;
import br.com.planner.exceptions.*;
import br.com.planner.mapper.ParticipantMapper;
import br.com.planner.mapper.TripMapper;
import br.com.planner.repositories.OwnerRepository;
import br.com.planner.repositories.TripRepository;
import br.com.planner.services.email.EmailService;
import br.com.planner.services.participant.ParticipantService;
import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
public class TripService {

    private TripRepository tripRepository;

    private ParticipantService participantService;

    private OwnerRepository ownerRepository;

    private ModelMapper modelMapper;

    private EmailService emailService;

    private ParticipantMapper participantMapper;

    private TripMapper tripMapper;

    public TripCreateResponseDTO create(TripRequestDTO tripRequestDTO, UUID ownerId) {
        tripDateValidation(tripRequestDTO.getStartsAt(), tripRequestDTO.getEndsAt());

        Owner owner = this.ownerRepository.findById(ownerId)
                .orElseThrow(() -> new NotFoundException("Owner not found"));

        Trip map = modelMapper.map(tripRequestDTO, Trip.class);
        map.setOwnerName(owner.getName());
        map.setOwnerEmail(owner.getEmail());
        map.setOwnerId(ownerId);

        Trip save = this.tripRepository.save(map);

        List<Participant> participants = this.participantService.registerParticipansToTrip(save.getId(), tripRequestDTO.getEmails_to_invite());

        List<Participant> participantsWithoutOwnerId = this.participantService.getParticipantsWithoutOwnerId(participants);

        List<String> emailsToSend = new ArrayList<>();

        for (Participant item : participantsWithoutOwnerId) {
            emailsToSend.add(item.getEmail());
        }

        Email email = new Email(owner.getEmail(),
                emailsToSend,
                "Viagem " + save.getDestination(),
                String.format("http://localhost:5173/create?tripId=%s", save.getId()));

        this.emailService.sendEmailToParticipant(email);

        return this.tripMapper.toTripCreateResponseDTO(save, participants);

    }

    public TripListPageableResponseDTO getAllTrips(int pageNumber, int pageSize, UUID ownerId) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        Page<Trip> tripsPageable = this.tripRepository.findAllByOwnerId(ownerId, pageable);

        return tripMapper.toTripListPageableResponseDTO(tripsPageable);
    }

    public TripResponseDTO getTripById(UUID tripId) {
        Trip trip = this.tripRepository.findById(tripId)
                .orElseThrow(() -> new NotFoundException("Trip not found"));

        List<Participant> participants = this.participantService.getParticipants(tripId);

        return tripMapper.toTripResponseDTO(trip, participants);
    }

    public TripResponseDTO findTripByDestinationFilter(String destination, UUID ownerId) {
        Trip trip = this.tripRepository.findByDestinationContainingIgnoreCaseAndOwnerId(destination, ownerId)
                .orElseThrow(() -> new NotFoundException("Trip not found"));

        List<Participant> participants = this.participantService.getParticipants(trip.getId());

        return tripMapper.toTripResponseDTO(trip, participants);
    }

    public UpdateTripDTO updateTrip(UUID tripId, UpdateTripDTO request) {

        tripDateValidation(request.getStartsAt(), request.getEndsAt());

        Trip tripRequest = this.tripRepository.findById(tripId)
                .orElseThrow(() -> new NotFoundException("Trip not found"));

        tripRequest.setDestination(request.getDestination());
        tripRequest.setStartsAt(request.getStartsAt());
        tripRequest.setEndsAt(request.getEndsAt());

        Trip trip = this.tripRepository.save(tripRequest);

        return modelMapper.map(trip, UpdateTripDTO.class);
    }

    public TripIdDto confirmTrip(UUID tripId) {
        Trip trip = this.tripRepository.findById(tripId)
                .orElseThrow(() -> new NotFoundException("Trip not found"));

        if (trip.isConfirmed()) {
            throw new TripAlreadyConfirmedException("Trip has already been confirmed.");
        }

        trip.setConfirmed(true);
        Trip updatedTrip = this.tripRepository.save(trip);

        return new TripIdDto(updatedTrip.getId());

    }

    public void deleteTripById(UUID tripId) {
        if (!tripRepository.existsById(tripId)) {
            throw new NotFoundException("Trip not found");
        }

        this.tripRepository.deleteById(tripId);
    }

    public TripResponseDTO sendInvites(TripInviteDTO request, UUID tripId) {
        Trip trip = this.tripRepository.findById(tripId)
                .orElseThrow(() -> new NotFoundException("Trip not found"));

        List<Participant> participants = this.participantService.registerParticipansToTrip(tripId, request.getEmailsToInvite());

        return tripMapper.toTripResponseDTO(trip, participants);
    }

    public TripListPageableResponseDTO getTripsForParticipants(int pageNumber, int pageSize, UUID ownerId) {
        this.ownerRepository.findById(ownerId)
                .orElseThrow(() -> new NotFoundException("Not found."));

        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        Page<Trip> tripsPageable = this.tripRepository.findTripsByParticipantOwnerId(ownerId, pageable);

        return this.tripMapper.toTripListPageableResponseDTO(tripsPageable);
    }

    private void tripDateValidation(LocalDateTime startsAt, LocalDateTime endsAt) {
        if (endsAt.isBefore(startsAt)) {
            throw new TripDateException("End date must be after start date.");
        }
    }
}
