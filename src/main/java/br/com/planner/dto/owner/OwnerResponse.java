package br.com.planner.dto.owner;

import br.com.planner.domain.Trip;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OwnerResponse {

    private String name;
    private String email;
    private List<Trip> trips;

}
