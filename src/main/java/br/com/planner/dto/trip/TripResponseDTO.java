package br.com.planner.dto.trip;


import br.com.planner.dto.participant.ParticipantResponseDTO;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class TripResponseDTO {
    private String destination;
    private LocalDateTime startsAt;
    private LocalDateTime endsAt;
    private String ownerName;
    private String ownerEmail;
    private boolean confirmed;
    private List<ParticipantResponseDTO> participants;
}
