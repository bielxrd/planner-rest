package br.com.planner.controllers;

import br.com.planner.domain.Owner;
import br.com.planner.dto.owner.OwnerRequestDTO;
import br.com.planner.dto.owner.OwnerResponse;
import br.com.planner.services.OwnerService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/owners")
public class OwnerController {

    private OwnerService ownerService;

    public OwnerController(OwnerService ownerService) {
        this.ownerService = ownerService;
    }

    @PostMapping("/create")
    public ResponseEntity<OwnerResponse> create(@RequestBody OwnerRequestDTO owner) {
        return ResponseEntity.ok().body(this.ownerService.create(owner));
    }
}
