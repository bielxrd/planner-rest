package br.com.planner.controllers;

import br.com.planner.dto.participant.ParticipantConfirmRequestDTO;
import br.com.planner.dto.participant.ParticipantResponseDTO;
import br.com.planner.services.ParticipantService;
import org.apache.coyote.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/participants")
public class ParticipantController {

    private ParticipantService participantService;

    public ParticipantController(ParticipantService participantService) {
        this.participantService = participantService;
    }

    @PatchMapping("/confirm/{tripId}")
    public ResponseEntity<ParticipantResponseDTO> confirm(@PathVariable UUID tripId, @RequestBody ParticipantConfirmRequestDTO request) {
        ParticipantResponseDTO participantResponseDTO = this.participantService.confirmTrip(request, tripId);
        return ResponseEntity.ok().body(participantResponseDTO);
    }


}
