package br.com.planner.dto.activity;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ActivityRequestDTO {
    private String name;
    private LocalDateTime occursAt;

}
