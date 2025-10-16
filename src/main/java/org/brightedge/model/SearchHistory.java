package org.brightedge.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "search_history", uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "city", "country"})
)

public class SearchHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String city;
    private String country;

    private LocalDateTime searchedAt;

    //  Add this relationship
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id") // column in search_history table
    private User user;

    public SearchHistory() {}

    public SearchHistory(String city, String country, LocalDateTime searchedAt, User user) {
        this.city = city;
        this.country = country;
        this.searchedAt = searchedAt;
        this.user = user;
    }


    public Long getId() { return id; }
    public String getCity() { return city; }
    public String getCountry() { return country; }
    public LocalDateTime getSearchedAt() { return searchedAt; }
    public User getUser() { return user; }

    public void setId(Long id) { this.id = id; }
    public void setCity(String city) { this.city = city; }
    public void setCountry(String country) { this.country = country; }
    public void setSearchedAt(LocalDateTime searchedAt) { this.searchedAt = searchedAt; }
    public void setUser(User user) { this.user = user; } // fixes search.setUser(user)
}