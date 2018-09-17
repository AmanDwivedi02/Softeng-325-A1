package nz.ac.auckland.concert.service.domain;

import nz.ac.auckland.concert.common.dto.CreditCardDTO;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "CREDITCARD")
public class CreditCard {

    @Id
    @GeneratedValue
    @Column(nullable = false, unique = true)
    private Long _id;

    public enum Type {Visa, Master};

    @Enumerated
    @Column(nullable = false)
    private CreditCardDTO.Type _type;

    @Column(nullable = false)
    private String _name;

    @Column(nullable = false)
    private String _number;

    @Column(nullable = false)
    private LocalDate _expiryDate;

    @ManyToOne
    @JoinColumn(nullable = false)
    private User _user;

    public CreditCard() {
    }

    public CreditCard(CreditCardDTO.Type _type, String _name, String _number, LocalDate _expiryDate, User _user) {
        this._type = _type;
        this._name = _name;
        this._number = _number;
        this._expiryDate = _expiryDate;
        this._user = _user;
    }

    public Long getId() {
        return _id;
    }

    public void setId(Long _id) {
        this._id = _id;
    }

    public CreditCardDTO.Type getType() {
        return _type;
    }

    public void setType(CreditCardDTO.Type _type) {
        this._type = _type;
    }

    public String getName() {
        return _name;
    }

    public void setName(String _name) {
        this._name = _name;
    }

    public String getNumber() {
        return _number;
    }

    public void setNumber(String _number) {
        this._number = _number;
    }

    public LocalDate getExpiryDate() {
        return _expiryDate;
    }

    public void setExpiryDate(LocalDate _expiryDate) {
        this._expiryDate = _expiryDate;
    }

    public User getUser() {
        return _user;
    }

    public void setUser(User _user) {
        this._user = _user;
    }
}
