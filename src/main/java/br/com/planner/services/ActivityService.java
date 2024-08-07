package br.com.planner.services;

import br.com.planner.domain.Activity;
import br.com.planner.dto.activity.ActivityRequestDTO;
import br.com.planner.dto.activity.ActivityResponseDTO;
import br.com.planner.dto.trip.TripResponseDTO;
import br.com.planner.exceptions.ActivityFoundException;
import br.com.planner.exceptions.ActivityNotFoundException;
import br.com.planner.exceptions.TripDateException;
import br.com.planner.mapper.ActivityMapper;
import br.com.planner.repositories.ActivityRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ActivityService {

    private ActivityRepository activityRepository;

    private TripService tripService;

    private ActivityMapper activityMapper;

    public ActivityService(ActivityRepository activityRepository, TripService tripService, ActivityMapper activityMapper) {
        this.activityRepository = activityRepository;
        this.tripService = tripService;
        this.activityMapper = activityMapper;
    }

    public void createActivityForTrip(UUID tripId, ActivityRequestDTO requestDTO) {
        TripResponseDTO tripFound = this.tripService.getTripById(tripId);

        this.activityRepository.findByNameAndTripId(requestDTO.getName(), tripId).ifPresentOrElse((activity) -> {
            throw new ActivityFoundException("Activity already registered.");
        }, () -> {

            if (!(tripFound.getStartsAt().isBefore(requestDTO.getOccursAt()) && tripFound.getEndsAt().isAfter(requestDTO.getOccursAt()))) { // se a atividade n estiver entre a data de inicio da viagem e termino
                throw new TripDateException("The activity must be registered between the start date and end date of the trip.");
            }

            Activity activity = Activity.builder()
                    .name(requestDTO.getName())
                    .occursAt(requestDTO.getOccursAt())
                    .tripId(tripId)
                    .build();

            this.activityRepository.save(activity);
        });
    }

    public List<ActivityResponseDTO> getActivitiesByFilter(String filter, UUID tripId) {
        List<Activity> activities;

        switch (filter) {
            case "future":
                activities = this.activityRepository.findAllByOccursAtAfterAndTripId(LocalDateTime.now(), tripId);
                break;
            case "past":
                activities = this.activityRepository.findAllByOccursAtBeforeAndTripId(LocalDateTime.now(), tripId);
                break;
            case "all":
                activities = this.activityRepository.findAllByTripId(tripId);
                break;
            default:
                throw new IllegalArgumentException("Invalid filter type: " + filter);
        }

        return activities.stream()
                .map(this.activityMapper::convertToDTO)
                .collect(Collectors.toList());
    }

    public void deleteActivityById(UUID id) {
        if (!this.activityRepository.existsById(id)) {
            throw new ActivityNotFoundException("Activity not found.");
        }

        this.activityRepository.deleteById(id);
    }
}
