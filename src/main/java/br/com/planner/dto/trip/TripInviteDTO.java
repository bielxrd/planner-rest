package br.com.planner.dto.trip;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Builder
@Data
public class TripInviteDTO {
    private List<String> emailsToInvite;
}
