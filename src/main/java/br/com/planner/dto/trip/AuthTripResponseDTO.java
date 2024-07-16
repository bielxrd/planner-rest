package br.com.planner.dto.trip;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthTripResponseDTO {
    private UUID id;
    private String destination;
    private LocalDateTime startsAt;
    private LocalDateTime endsAt;
    private boolean confirmed;
    private UUID ownerId;
}
