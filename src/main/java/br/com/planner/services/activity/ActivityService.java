package br.com.planner.services.activity;

import br.com.planner.domain.Activity;
import br.com.planner.dto.activity.ActivityRequestDTO;
import br.com.planner.dto.activity.ActivityResponseDTO;
import br.com.planner.dto.participant.ParticipantResponseDTO;
import br.com.planner.dto.trip.TripResponseDTO;
import br.com.planner.exceptions.AlreadyExistsException;
import br.com.planner.exceptions.InvalidInputException;
import br.com.planner.exceptions.NotFoundException;
import br.com.planner.mapper.ActivityMapper;
import br.com.planner.repositories.ActivityRepository;
import br.com.planner.services.sqs.SQSProducerService;
import br.com.planner.services.trip.TripService;
import com.amazonaws.services.sqs.model.SendMessageResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ActivityService {

    private final ActivityRepository activityRepository;

    private final TripService tripService;

    private final ActivityMapper activityMapper;

    private final SQSProducerService sqsProducerService;

    public void createActivityForTrip(UUID tripId, ActivityRequestDTO requestDTO) {
        TripResponseDTO tripFound = this.tripService.getTripById(tripId);

        this.activityRepository.findByNameAndTripId(requestDTO.getName(), tripId).ifPresentOrElse((activity) -> {
            throw new AlreadyExistsException("Activity already registered.");
        }, () -> {

            if (!(tripFound.getStartsAt().isBefore(requestDTO.getOccursAt()) && tripFound.getEndsAt().isAfter(requestDTO.getOccursAt()))) { // se a atividade n estiver entre a data de inicio da viagem e termino
                throw new InvalidInputException("The activity must be registered between the start date and end date of the trip.");
            }

            Activity activity = Activity.builder()
                    .name(requestDTO.getName())
                    .occursAt(requestDTO.getOccursAt())
                    .tripId(tripId)
                    .build();

            this.activityRepository.save(activity);

            for (ParticipantResponseDTO participant : tripFound.getParticipants()) {
                this.sqsProducerService.sendEmailToQueue(participant.getEmail(), "activity_queue", tripId.toString());
            }

        });
    }

    public List<ActivityResponseDTO> getActivitiesByFilter(String filter, UUID tripId) {
        List<Activity> activities = switch (filter) {
            case "future" -> this.activityRepository.findAllByOccursAtAfterAndTripId(LocalDateTime.now(), tripId);
            case "past" -> this.activityRepository.findAllByOccursAtBeforeAndTripId(LocalDateTime.now(), tripId);
            case "all" -> this.activityRepository.findAllByTripId(tripId);
            default -> throw new IllegalArgumentException("Invalid filter type: " + filter);
        };

        return activities.stream()
                .map(this.activityMapper::convertToDTO)
                .collect(Collectors.toList());
    }

    public void deleteActivityById(UUID id) {
        if (!this.activityRepository.existsById(id)) {
            throw new NotFoundException("Activity not found.");
        }

        this.activityRepository.deleteById(id);
    }
}
