package nz.ac.auckland.concert.service.domain;

import nz.ac.auckland.concert.common.dto.SeatDTO;
import nz.ac.auckland.concert.common.types.PriceBand;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Table(name = "BOOKING")
public class Booking {

    @Id
    @GeneratedValue
    @Column(nullable = false, unique = true)
    private Long _id;

    @ManyToOne
    @JoinColumn(nullable = false)
    private Concert _concert;

    @ManyToOne
    @JoinColumn(nullable = false)
    private User _user;

    @Column(nullable = false)
    private LocalDateTime _dateTime;

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable
    private Set<Seat> _seats;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PriceBand _priceBand;

    @Column(nullable = false)
    private LocalDateTime _expiryTime;

    @Column(nullable = false)
    private boolean _booked;

    public Booking() {
    }

    public Booking(Concert _concert, User _user, LocalDateTime _dateTime, Set<Seat> _seats, PriceBand _priceBand, LocalDateTime _expiryTime, boolean _booked) {
        this._concert = _concert;
        this._user = _user;
        this._dateTime = _dateTime;
        this._seats = _seats;
        this._priceBand = _priceBand;
        this._expiryTime = _expiryTime;
        this._booked = _booked;
    }

    public Booking(Long _id, Concert _concert, User _user, LocalDateTime _dateTime, Set<Seat> _seats, PriceBand _priceBand) {
        this._id = _id;
        this._concert = _concert;
        this._user = _user;
        this._dateTime = _dateTime;
        this._seats = _seats;
        this._priceBand = _priceBand;
    }

    public Long getId() {
        return _id;
    }

    public void setId(Long _id) {
        this._id = _id;
    }

    public Concert getConcert() {
        return _concert;
    }

    public void setConcert(Concert _concert) {
        this._concert = _concert;
    }

    public User getUser() {
        return _user;
    }

    public void setUser(User _user) {
        this._user = _user;
    }

    public LocalDateTime getDateTime() {
        return _dateTime;
    }

    public void setDateTime(LocalDateTime _dateTime) {
        this._dateTime = _dateTime;
    }

    public Set<Seat> getSeats() {
        return _seats;
    }

    public void setSeats(Set<Seat> _seats) {
        this._seats = _seats;
    }

    public PriceBand getPriceBand() {
        return _priceBand;
    }

    public void setPriceBand(PriceBand _priceBand) {
        this._priceBand = _priceBand;
    }

    public LocalDateTime getExpiryTime() {
        return _expiryTime;
    }

    public void setExpiryTime(LocalDateTime _expiryTime) {
        this._expiryTime = _expiryTime;
    }

    public boolean isBooked() {
        return _booked;
    }

    public void setBooked(boolean _booked) {
        this._booked = _booked;
    }
}
