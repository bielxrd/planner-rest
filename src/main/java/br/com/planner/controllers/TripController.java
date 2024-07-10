package br.com.planner.controllers;

import br.com.planner.dto.trip.TripRequestDTO;
import br.com.planner.dto.trip.TripCreateResponseDTO;
import br.com.planner.dto.trip.TripResponseDTO;
import br.com.planner.services.TripService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/trips")
public class TripController {

    private TripService tripService;

    public TripController(TripService tripService) {
        this.tripService = tripService;
    }

    @PostMapping
    public ResponseEntity<TripCreateResponseDTO> create(@RequestBody TripRequestDTO requestDTO) {
        TripCreateResponseDTO tripResponse = this.tripService.create(requestDTO);
        return ResponseEntity.ok(tripResponse);
    }

    @GetMapping("/{tripId}")
    public ResponseEntity<TripResponseDTO> get(@PathVariable UUID tripId) {
        TripResponseDTO trip = this.tripService.getTripById(tripId);
        return ResponseEntity.ok(trip);
    }
}
