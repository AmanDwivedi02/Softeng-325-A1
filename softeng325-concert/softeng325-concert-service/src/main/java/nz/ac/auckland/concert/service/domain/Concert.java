package nz.ac.auckland.concert.service.domain;

import nz.ac.auckland.concert.common.types.PriceBand;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Set;

@Entity
public class Concert {

    //TODO: Map the following Concert variables to database
    @Id
    @GeneratedValue
    private Long _id;
    private String _title;
    private Set<LocalDateTime> _dates;
    @MapKeyEnumerated(EnumType.STRING)
    private Map<PriceBand, BigDecimal> _tariff;
    @ManyToMany
    private Set<Long> _performerIds;

    public Long getId(){
        return _id;
    }

    public void setId(Long id){
        this._id = id;
    }

    public String getTitle(){
        return _title;
    }

    public void setTitle(String title){
        this._title = title;
    }

    public Set<LocalDateTime> getDates(){
        return _dates;
    }

    public void setDates(Set<LocalDateTime> dates){
        this._dates = dates;
    }

    public Map<PriceBand, BigDecimal> getTariff(){
        return _tariff;
    }

    public void setTariff(Map<PriceBand, BigDecimal> tariff){
        this._tariff = tariff;
    }

    public Set<Long> getPerformerIds(){
        return _performerIds;
    }

    public  void setPerformerIds(Set<Long> performerIds){
        this._performerIds = performerIds;
    }
}
