package nz.ac.auckland.concert.service.mappers;

import nz.ac.auckland.concert.common.dto.SeatDTO;
import nz.ac.auckland.concert.service.domain.Seat;

public class SeatMapper {
    public static Seat toDomain(SeatDTO seatDTO){
        return new Seat(
                seatDTO.getRow(),
                seatDTO.getNumber()
        );
    }

    public static SeatDTO toDTO(Seat seat){
        return new SeatDTO(
                seat.getRow(),
                seat.getNumber()
        );
    }
}
