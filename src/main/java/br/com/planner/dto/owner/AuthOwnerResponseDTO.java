package br.com.planner.dto.owner;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class AuthOwnerResponseDTO {
    private String token;
    private OwnerResponse owner;
}
