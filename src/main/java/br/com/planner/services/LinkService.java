package br.com.planner.services;

import br.com.planner.domain.Link;
import br.com.planner.dto.link.LinkDTO;
import br.com.planner.mapper.LinkMapper;
import br.com.planner.repositories.LinkRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class LinkService {

    private LinkRepository linkRepository;
    private LinkMapper linkMapper;

    public LinkService(LinkRepository linkRepository, LinkMapper linkMapper) {
        this.linkRepository = linkRepository;
        this.linkMapper = linkMapper;
    }

    public void createLinkToTrip(LinkDTO requestDTO, UUID tripId) {
        Link newLink = new Link();
        newLink.setUrl(requestDTO.getUrl());
        newLink.setTitle(requestDTO.getTitle());
        newLink.setTripId(tripId);

        this.linkRepository.save(newLink);
    }

    public List<LinkDTO> getLinksByTripId(UUID tripId) {
        List<Link> links = this.linkRepository.findAllByTripId(tripId);

        if (links.isEmpty()) {
            throw new RuntimeException("There is no links registered on that trip.");
        }

        return linkMapper.convertToLinkDTO(links);
    }

    public void deleteLinkById(UUID id) {
        if (!this.linkRepository.existsById(id)) {
            throw new RuntimeException("Link not found.");
        }

        this.linkRepository.deleteById(id);
    }
}
