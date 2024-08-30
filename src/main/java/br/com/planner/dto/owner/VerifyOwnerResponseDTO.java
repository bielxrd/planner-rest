package br.com.planner.dto.owner;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Builder
@Data
public class VerifyOwnerResponseDTO {

    private boolean owner_exists;
    private UUID ownerId;

}
