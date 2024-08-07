package br.com.planner.mapper;

import br.com.planner.domain.Activity;
import br.com.planner.dto.activity.ActivityResponseDTO;
import org.springframework.stereotype.Component;

@Component
public class ActivityMapper {

    public ActivityResponseDTO convertToDTO(Activity activity) {
        return ActivityResponseDTO.builder()
                .id(activity.getId())
                .name(activity.getName())
                .occursAt(activity.getOccursAt())
                .build();
    }
}
