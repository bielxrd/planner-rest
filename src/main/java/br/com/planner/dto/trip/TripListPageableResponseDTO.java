package br.com.planner.dto.trip;

import br.com.planner.dto.participant.ParticipantResponseDTO;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TripListPageableResponseDTO {
    private List<TripResponseDTO> trips;
    @JsonProperty(namespace = "page_number")
    private int pageNumber;
    @JsonProperty(namespace = "page_size")
    private int pageSize;
    @JsonProperty(namespace = "total_pages")
    private int totalPages;
}
