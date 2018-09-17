package nz.ac.auckland.concert.service.mappers;

import nz.ac.auckland.concert.common.dto.BookingDTO;
import nz.ac.auckland.concert.common.dto.SeatDTO;
import nz.ac.auckland.concert.service.domain.Booking;
import nz.ac.auckland.concert.service.domain.Seat;

import java.util.HashSet;
import java.util.Set;

public class BookingMapper {
    public static BookingDTO toDTO(Booking booking){
        Set<SeatDTO> seatSet = new HashSet<>();
        for (Seat seat : booking.getSeats()) {
            seatSet.add(SeatMapper.toDTO(seat));
        }
        return new BookingDTO(
                booking.getConcert().getId(),
                booking.getConcert().getTitle(),
                booking.getDateTime(),
                seatSet,
                booking.getPriceBand()
        );
    }
}
