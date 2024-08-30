package br.com.planner.dto.owner;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Builder
@Data
public class AssignOwnerRequest {

    private String email;
    private String password;
    private UUID ownerId;

}
