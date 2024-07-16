package br.com.planner.controllers;

import br.com.planner.domain.Owner;
import br.com.planner.dto.owner.AuthOwnerRequestDTO;
import br.com.planner.dto.owner.AuthOwnerResponseDTO;
import br.com.planner.dto.owner.OwnerRequestDTO;
import br.com.planner.dto.owner.OwnerResponse;
import br.com.planner.services.OwnerService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/owners")
@CrossOrigin(origins = "*")
public class OwnerController {

    private OwnerService ownerService;

    public OwnerController(OwnerService ownerService) {
        this.ownerService = ownerService;
    }

    @PostMapping("/create")
    public ResponseEntity<OwnerResponse> create(@RequestBody OwnerRequestDTO owner) {
        return ResponseEntity.ok().body(this.ownerService.create(owner));
    }

    @PostMapping("/auth")
    public ResponseEntity<AuthOwnerResponseDTO> auth(@RequestBody AuthOwnerRequestDTO authOwnerRequest) {
        AuthOwnerResponseDTO owner = this.ownerService.auth(authOwnerRequest);
        return ResponseEntity.ok().body(owner);
    }

}
