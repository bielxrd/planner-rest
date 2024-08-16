package br.com.planner.mapper;

import br.com.planner.domain.Participant;
import br.com.planner.dto.participant.ParticipantResponseDTO;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ParticipantMapper {

    private ModelMapper modelMapper;

    public ParticipantMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    public List<ParticipantResponseDTO> toListParticipantResponseDTO(List<Participant> participants) {
        return participants.stream()
                .map(participant -> modelMapper.map(participant, ParticipantResponseDTO.class))
                .toList();
    }
}
