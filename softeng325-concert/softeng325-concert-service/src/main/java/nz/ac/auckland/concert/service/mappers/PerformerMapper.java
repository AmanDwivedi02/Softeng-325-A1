package nz.ac.auckland.concert.service.mappers;

import nz.ac.auckland.concert.common.dto.PerformerDTO;
import nz.ac.auckland.concert.service.domain.Concert;
import nz.ac.auckland.concert.service.domain.Performer;

import java.util.HashSet;
import java.util.Set;

public class PerformerMapper {

    public static PerformerDTO toDTO(Performer performer){
        Set<Long> concertIds = new HashSet<Long>();
        for (Concert concert : performer.getConcerts()) {
            concertIds.add(concert.getId());
        }
        return new PerformerDTO(
                performer.getId(),
                performer.getName(),
                performer.getImageName(),
                performer.getGenre(),
                concertIds
        );
    }
}
