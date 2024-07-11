package br.com.planner.dto.trip;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TripRequestDTO {

    private String destination;

    @JsonProperty("starts_at")
    private LocalDateTime startsAt;

    @JsonProperty("ends_at")
    private LocalDateTime endsAt;

    @JsonProperty("emails_to_invite")
    private List<String> emails_to_invite;

}
