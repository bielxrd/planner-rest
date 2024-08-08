package br.com.planner.mapper;

import br.com.planner.domain.Link;
import br.com.planner.dto.link.LinkDTO;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class LinkMapper {

    public List<LinkDTO> convertToLinkDTO(List<Link> links) {
        return links.stream()
                .map(link -> LinkDTO.builder()
                        .url(link.getUrl())
                        .title(link.getTitle())
                        .build())
                .collect(Collectors.toList());
    }

}
