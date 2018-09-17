package nz.ac.auckland.concert.service.domain;

import javax.persistence.*;

@Entity
@Table(name = "USER")
public class User {

    @Id
    @Column(nullable = false, unique = true)
    private String _username;

    @Column(nullable = false)
    private String _password;

    @Column(nullable = false)
    private String _firstname;

    @Column(nullable = false)
    private String _lastname;

    @Column(nullable = false)
    private String _authToken;

    public User() {
    }

    public User(String _username, String _password, String _firstname, String _lastname) {
        this._username = _username;
        this._password = _password;
        this._firstname = _firstname;
        this._lastname = _lastname;
    }

    public String getUsername() {
        return _username;
    }

    public void setUsername(String _username) {
        this._username = _username;
    }

    public String getPassword() {
        return _password;
    }

    public void setPassword(String _password) {
        this._password = _password;
    }

    public String getFirstname() {
        return _firstname;
    }

    public void setFirstname(String _firstname) {
        this._firstname = _firstname;
    }

    public String getLastname() {
        return _lastname;
    }

    public void setLastname(String _lastname) {
        this._lastname = _lastname;
    }

    public String getAuthToken() {
        return _authToken;
    }

    public void setAuthToken(String _authToken) {
        this._authToken = _authToken;
    }
}
