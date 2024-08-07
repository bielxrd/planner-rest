package br.com.planner.services;

import br.com.planner.domain.Activity;
import br.com.planner.dto.activity.ActivityRequestDTO;
import br.com.planner.dto.trip.TripResponseDTO;
import br.com.planner.exceptions.ActivityFoundException;
import br.com.planner.exceptions.ActivityNotFoundException;
import br.com.planner.repositories.ActivityRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class ActivityService {

    private ActivityRepository activityRepository;

    private TripService tripService;

    public ActivityService(ActivityRepository activityRepository, TripService tripService) {
        this.activityRepository = activityRepository;
        this.tripService = tripService;
    }

    public void createActivityForTrip(UUID tripId, ActivityRequestDTO requestDTO) {
        TripResponseDTO tripFound = this.tripService.getTripById(tripId);

        this.activityRepository.findByNameAndTripId(requestDTO.getName(), tripId).ifPresentOrElse((activity) -> {
            throw new ActivityFoundException("Activity already registered.");
        }, () -> {
            Activity activity = Activity.builder()
                    .name(requestDTO.getName())
                    .occursAt(requestDTO.getOccursAt())
                    .tripId(tripId)
                    .build();

            this.activityRepository.save(activity);
        });
    }

    
}
