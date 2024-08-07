package br.com.planner.dto.activity;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class ActivityResponseDTO {
    private UUID id;
    private String name;
    private LocalDateTime occursAt;
}
