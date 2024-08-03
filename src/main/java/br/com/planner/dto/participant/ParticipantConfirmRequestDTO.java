package br.com.planner.dto.participant;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ParticipantConfirmRequestDTO {
    private String name;
}
