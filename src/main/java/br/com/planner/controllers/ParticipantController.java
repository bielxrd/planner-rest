package br.com.planner.controllers;

import br.com.planner.dto.owner.*;
import br.com.planner.dto.participant.ParticipantConfirmRequestDTO;
import br.com.planner.dto.participant.ParticipantResponseDTO;
import br.com.planner.services.participant.ParticipantService;
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

    @GetMapping("/verify-owner")
    public ResponseEntity<VerifyOwnerResponseDTO> verifyOwnerExists(@RequestBody VerifyOwnerRequestDTO verifyOwnerRequestDTO) {
        VerifyOwnerResponseDTO verifyOwnerResponseDTO = this.participantService.verifyOwnerExistingAccount(verifyOwnerRequestDTO.getEmail());
        return ResponseEntity.ok(verifyOwnerResponseDTO);
    }

    @PostMapping("/assign/{tripId}")
    public ResponseEntity<OwnerResponse> assignToOwnerAlreadyRegistered(@PathVariable UUID tripId, @RequestBody AssignOwnerRequest ownerRequest) {
        OwnerResponse ownerResponse = this.participantService.assignParticipantToOwnerAlreadyCreated(tripId, ownerRequest);
        return ResponseEntity.ok().body(ownerResponse);
    }

    @PostMapping("/create/{tripId}")
    public ResponseEntity<OwnerResponse> create(@PathVariable UUID tripId, @RequestBody OwnerRequestDTO requestDTO) {
        OwnerResponse response = this.participantService.assignParticipantToOwner(tripId, requestDTO);
        return ResponseEntity.ok().body(response);
    }


}
