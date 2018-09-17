package nz.ac.auckland.concert.service.domain;

import nz.ac.auckland.concert.common.types.SeatNumber;
import nz.ac.auckland.concert.common.types.SeatRow;

import javax.persistence.*;

@Entity
@Table(name = "SEAT")
public class Seat {

    @Id
    @GeneratedValue
    private Long _id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SeatRow _row;

    @Column(nullable = false)
    private SeatNumber _number;

    public Seat() {
    }

    public Seat(SeatRow _row, SeatNumber _number) {
        this._row = _row;
        this._number = _number;
    }

    public Long getId() {
        return _id;
    }

    public void setId(Long _id) {
        this._id = _id;
    }

    public SeatRow getRow() {
        return _row;
    }

    public void setRow(SeatRow _row) {
        this._row = _row;
    }

    public SeatNumber getNumber() {
        return _number;
    }

    public void setNumber(SeatNumber _number) {
        this._number = _number;
    }
}
