package br.com.planner.services.link;

import br.com.planner.domain.Link;
import br.com.planner.dto.link.LinkDTO;
import br.com.planner.dto.participant.ParticipantResponseDTO;
import br.com.planner.dto.trip.TripResponseDTO;
import br.com.planner.exceptions.NotFoundException;
import br.com.planner.mapper.LinkMapper;
import br.com.planner.repositories.LinkRepository;
import br.com.planner.services.sqs.SQSProducerService;
import br.com.planner.services.trip.TripService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class LinkService {

    private final LinkRepository linkRepository;

    private final LinkMapper linkMapper;

    private final TripService tripService;

    private final SQSProducerService sqsProducerService;


    public void createLinkToTrip(LinkDTO requestDTO, UUID tripId) {

        TripResponseDTO trip = this.tripService.getTripById(tripId);

        Link newLink = new Link();
        newLink.setUrl(requestDTO.getUrl());
        newLink.setTitle(requestDTO.getTitle());
        newLink.setTripId(tripId);

        this.linkRepository.save(newLink);

        for (ParticipantResponseDTO participant : trip.getParticipants()) {
            this.sqsProducerService.sendEmailToQueue(participant.getEmail(), "link_queue", tripId.toString());
        }

    }

    public List<LinkDTO> getLinksByTripId(UUID tripId) {
        List<Link> links = this.linkRepository.findAllByTripId(tripId);

        if (links.isEmpty()) {
            throw new NotFoundException("There is no links registered on that trip.");
        }

        return linkMapper.convertToLinkDTO(links);
    }

    public void deleteLinkById(UUID id) {
        if (!this.linkRepository.existsById(id)) {
            throw new NotFoundException("Link not found.");
        }

        this.linkRepository.deleteById(id);
    }
}
