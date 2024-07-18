package br.com.planner.controllers;

import br.com.planner.dto.trip.*;
import br.com.planner.services.TripService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping("/trips")
public class TripController {

    private TripService tripService;

    public TripController(TripService tripService) {
        this.tripService = tripService;
    }

    @PostMapping
    public ResponseEntity<TripCreateResponseDTO> create(@RequestBody TripRequestDTO requestDTO, HttpServletRequest request) {

        Object ownerId = request.getAttribute("owner_id");

        if (ownerId == null) {
            return ResponseEntity.badRequest().build();
        }

        TripCreateResponseDTO tripResponse = this.tripService.create(requestDTO, UUID.fromString(ownerId.toString()));
        return ResponseEntity.ok(tripResponse);
    }

    @GetMapping("/{tripId}")
    public ResponseEntity<TripResponseDTO> get(@PathVariable UUID tripId) {
        TripResponseDTO trip = this.tripService.getTripById(tripId);
        return ResponseEntity.ok(trip);
    }

    @PutMapping("/update/{tripId}")
    public ResponseEntity<UpdateTripDTO> update(@PathVariable UUID tripId, @RequestBody UpdateTripDTO request) {
        UpdateTripDTO updatedTrip = this.tripService.updateTrip(tripId, request);
        return ResponseEntity.ok(updatedTrip);
    }

    @PatchMapping("/{tripId}/confirm")
    public ResponseEntity<TripIdDto> confirm(@PathVariable UUID tripId, UriComponentsBuilder uriComponentsBuilder) {
        TripIdDto tripIdDto = this.tripService.confirmTrip(tripId);
        URI uri = uriComponentsBuilder.path("/trips/{tripId}").buildAndExpand(tripId).toUri();

        return ResponseEntity.created(uri).body(tripIdDto);
    }
}
