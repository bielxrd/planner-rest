package br.com.planner.dto.trip;

import br.com.planner.dto.participant.ParticipantResponseDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TripCreateResponseDTO {
    private UUID tripId;
    private String destination;
    private List<ParticipantResponseDTO> participants;
}
