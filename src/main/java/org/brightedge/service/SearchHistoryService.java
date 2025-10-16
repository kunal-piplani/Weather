package org.brightedge.service;

import jakarta.transaction.Transactional;
import org.brightedge.model.SearchHistory;
import org.brightedge.model.User;
import org.brightedge.repository.SearchHistoryRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class SearchHistoryService {

    private final SearchHistoryRepository repository;

    public SearchHistoryService(SearchHistoryRepository repository) {
        this.repository = repository;
    }

    @Transactional

    public void addSearch(String city, String country, User user) {
        repository.findByUser_IdAndCityIgnoreCaseAndCountryIgnoreCase(user.getId(), city, country)
                .ifPresentOrElse(existing -> {
                    existing.setSearchedAt(LocalDateTime.now());
                    repository.save(existing);
                }, () -> {
                    SearchHistory s = new SearchHistory();
                    s.setCity(city);
                    s.setCountry(country);
                    s.setSearchedAt(LocalDateTime.now());
                    s.setUser(user);
                    repository.save(s);
                });
    }

    public List<SearchHistory> getRecentSearches(User user) {
        return repository.findRecentSearchesByUser(user.getId());
    }
    @Transactional
    public void clearHistory(User user) {
        repository.deleteByUser_Id(user.getId());
    }
}