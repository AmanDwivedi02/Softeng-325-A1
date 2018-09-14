package nz.ac.auckland.concert.service.domain;

import nz.ac.auckland.concert.common.dto.SeatDTO;
import nz.ac.auckland.concert.common.types.PriceBand;

import java.time.LocalDateTime;
import java.util.Set;

public class Booking {

    //TODO: Add database mapping to Booking variables. Look into booking id
    private Long _concertId;
    private String _concertTitle;
    private LocalDateTime _dateTime;
    private Set<SeatDTO> _seats;
    private PriceBand _priceBand;

    public Long getConcertId() {
        return _concertId;
    }

    public void setConcertId(Long concertId) {
        this._concertId = concertId;
    }

    public String get_concertTitle() {
        return _concertTitle;
    }

    public void setConcertTitle(String concertTitle) {
        this._concertTitle = concertTitle;
    }

    public LocalDateTime getDateTime() {
        return _dateTime;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this._dateTime = dateTime;
    }

    public Set<SeatDTO> getSeats() {
        return _seats;
    }

    public void setSeats(Set<SeatDTO> seats) {
        this._seats = seats;
    }

    public PriceBand getPriceBand() {
        return _priceBand;
    }

    public void setPriceBand(PriceBand priceBand) {
        this._priceBand = priceBand;
    }
}
