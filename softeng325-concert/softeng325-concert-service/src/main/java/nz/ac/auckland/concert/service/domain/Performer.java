package nz.ac.auckland.concert.service.domain;

import nz.ac.auckland.concert.common.types.Genre;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import java.util.Set;

@Entity
@Table(name="PERFORMERS")
public class Performer {

    @Id
    @GeneratedValue
    @Column(nullable = false)
    private Long _id;

    @Column(nullable = false)
    private String _name;

    @Column(nullable = true)
    private String _imageName;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Genre _genre;

    @ManyToMany
    @JoinTable
    @Column(nullable = false)
    private Set<Concert> _concerts;

    public Long getId() {
        return _id;
    }

    public void setId(Long _id) {
        this._id = _id;
    }

    public String getName() {
        return _name;
    }

    public void setName(String _name) {
        this._name = _name;
    }

    public String getImageName() {
        return _imageName;
    }

    public void setImageName(String _imageName) {
        this._imageName = _imageName;
    }

    public Genre getGenre() {
        return _genre;
    }

    public void setGenre(Genre _genre) {
        this._genre = _genre;
    }

    public Set<Concert> getConcerts() {
        return _concerts;
    }

    public void setConcerts(Set<Concert> _concerts) {
        this._concerts = _concerts;
    }
}