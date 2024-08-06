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

    @GetMapping
    public ResponseEntity<TripListPageableResponseDTO> getTrips(@RequestParam(value = "page_number", required = false, defaultValue = "0") int pageNumber, @RequestParam(value = "page_size", required = false, defaultValue = "5") int pageSize, HttpServletRequest request) {
        Object ownerId = request.getAttribute("owner_id");

        if (ownerId == null) {
            return ResponseEntity.badRequest().build();
        }

        TripListPageableResponseDTO trips = this.tripService.getAllTrips(pageNumber, pageSize, UUID.fromString(ownerId.toString()));
        return ResponseEntity.ok().body(trips);
    }

    @GetMapping("/{tripId}")
    public ResponseEntity<TripResponseDTO> get(@PathVariable UUID tripId) {
        TripResponseDTO trip = this.tripService.getTripById(tripId);
        return ResponseEntity.ok(trip);
    }

    @GetMapping("/find/{destination}")
    public ResponseEntity<TripResponseDTO> find(@PathVariable String destination, HttpServletRequest request) {

        Object ownerId = request.getAttribute("owner_id");

        if (ownerId == null) {
            return ResponseEntity.badRequest().build();
        }

        TripResponseDTO tripByDestination = this.tripService.findTripByDestinationFilter(destination, UUID.fromString(ownerId.toString()));
        return ResponseEntity.ok().body(tripByDestination);
    }

    @PutMapping("/update/{tripId}")
    public ResponseEntity<UpdateTripDTO> update(@PathVariable UUID tripId, @RequestBody UpdateTripDTO request) {
        UpdateTripDTO updatedTrip = this.tripService.updateTrip(tripId, request);
        return ResponseEntity.ok(updatedTrip);
    }

    @PostMapping("/invite/{tripId}")
    public ResponseEntity<TripResponseDTO> invite(@PathVariable UUID tripId, @RequestBody TripInviteDTO request) {
        TripResponseDTO tripResponseDTO = this.tripService.sendInvites(request, tripId);
        return ResponseEntity.ok().body(tripResponseDTO);
    }

    @PatchMapping("/{tripId}/confirm")
    public ResponseEntity<TripIdDto> confirm(@PathVariable UUID tripId, UriComponentsBuilder uriComponentsBuilder) {
        TripIdDto tripIdDto = this.tripService.confirmTrip(tripId);
        URI uri = uriComponentsBuilder.path("/trips/{tripId}").buildAndExpand(tripId).toUri();

        return ResponseEntity.created(uri).body(tripIdDto);
    }
}
