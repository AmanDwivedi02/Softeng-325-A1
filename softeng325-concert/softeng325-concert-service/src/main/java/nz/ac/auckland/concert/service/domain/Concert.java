package nz.ac.auckland.concert.service.domain;

import nz.ac.auckland.concert.common.dto.ConcertDTO;
import nz.ac.auckland.concert.common.types.PriceBand;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.MapKeyColumn;
import javax.persistence.MapKeyEnumerated;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Set;

@Entity
@Table(name="CONCERTS")
public class Concert {

    @Id
    @GeneratedValue
    @Column(nullable = false, unique = true)
    private Long _id;

    @Column(nullable = false)
    private String _title;

    @ElementCollection
    @CollectionTable(name = "CONCERT_DATES")
    @Column(nullable = false, unique = true)
    private Set<LocalDateTime> _dates;

    @ElementCollection
    @JoinTable(name = "CONCERT_TARIFS")
    @MapKeyColumn(name = "price_band")
    @Column(nullable = false)
    @MapKeyEnumerated(EnumType.STRING)
    private Map<PriceBand, BigDecimal> _tariff;

    @ManyToMany
    @JoinTable(name = "CONCERT_PERFORMER")
    @Column(nullable = false, unique = true)
    private Set<Performer> _performers;

    public Long getId() {
        return _id;
    }

    public void setId(Long _id) {
        this._id = _id;
    }

    public String getTitle() {
        return _title;
    }

    public void setTitle(String _title) {
        this._title = _title;
    }

    public Set<LocalDateTime> getDates() {
        return _dates;
    }

    public void setDates(Set<LocalDateTime> _dates) {
        this._dates = _dates;
    }

    public Map<PriceBand, BigDecimal> getTariff() {
        return _tariff;
    }

    public void setTariff(Map<PriceBand, BigDecimal> _tariff) {
        this._tariff = _tariff;
    }

    public Set<Performer> getPerformers() {
        return _performers;
    }

    public void setPerformers(Set<Performer> _performerIds) {
        this._performers = _performerIds;
    }
}