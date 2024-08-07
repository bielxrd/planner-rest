package br.com.planner.dto.activity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ActivityRequestDTO {
    private String name;
    @JsonProperty(namespace = "occurs_at")
    private LocalDateTime occursAt;

}
