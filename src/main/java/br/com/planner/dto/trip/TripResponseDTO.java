package br.com.planner.dto.trip;


import br.com.planner.dto.participant.ParticipantResponseDTO;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class TripResponseDTO {
    private String destination;
    private LocalDateTime startsAt;
    private LocalDateTime endsAt;
    private String ownerName;
    private String ownerEmail;
    private List<ParticipantResponseDTO> participants;
}
