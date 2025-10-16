package org.brightedge.repository;

import org.brightedge.model.SearchHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.List;

public interface SearchHistoryRepository extends JpaRepository<SearchHistory, Long> {

    // Last 5 for a given user (SQLite native SQL to get LIMIT)
    @Query(value = "SELECT * FROM search_history WHERE user_id = :userId ORDER BY searched_at DESC LIMIT 5", nativeQuery = true)
    List<SearchHistory> findRecentSearchesByUser(@Param("userId") Long userId);

    // To avoid duplicates per user+city+country
    Optional<SearchHistory> findByUser_IdAndCityIgnoreCaseAndCountryIgnoreCase(Long userId, String city, String country);

    // Clear all for a user
    void deleteByUser_Id(Long userId);
}