package nz.ac.auckland.concert.service.mappers;

import nz.ac.auckland.concert.common.dto.ConcertDTO;
import nz.ac.auckland.concert.service.domain.Concert;
import nz.ac.auckland.concert.service.domain.Performer;

import java.util.HashSet;
import java.util.Set;

public class ConcertMapper {

    public static ConcertDTO toDTO(Concert concert){
        Set<Long> performerIds = new HashSet<Long>();
        for (Performer performer : concert.getPerformers()) {
            performerIds.add(performer.getId());
        }
        return new ConcertDTO(
                concert.getId(),
                concert.getTitle(),
                concert.getDates(),
                concert.getTariff(),
                performerIds
        );
    }
}
