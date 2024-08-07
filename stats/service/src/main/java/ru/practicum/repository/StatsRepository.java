package ru.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.ResponseStatsDto;
import ru.practicum.model.Stats;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface StatsRepository extends JpaRepository<Stats, Long> {

    @Query("SELECT new ru.practicum.ResponseStatsDto(s.app, s.uri, COUNT (s.ip))" +
            "FROM Stats s " +
            "WHERE s.timestamp BETWEEN ?1 AND ?2 " +
            "GROUP BY s.app, s.uri " +
            "ORDER BY COUNT (s.ip) DESC")
    List<ResponseStatsDto> findAllByTime(LocalDateTime start, LocalDateTime end);

    @Query("SELECT new ru.practicum.ResponseStatsDto(s.app, s.uri, COUNT (s.ip))" +
            "FROM Stats s " +
            "WHERE s.timestamp BETWEEN ?1 AND ?2 " +
            "GROUP BY s.app, s.uri " +
            "ORDER BY COUNT (s.ip) DESC")
    List<ResponseStatsDto> findAllByTimeUnique(LocalDateTime start, LocalDateTime end);

    @Query("SELECT new ru.practicum.ResponseStatsDto(s.app, s.uri, COUNT (s.ip))" +
            "FROM Stats s " +
            "WHERE s.timestamp BETWEEN ?1 AND ?2 AND s.uri IN ?3 " +
            "GROUP BY s.app, s.uri " +
            "ORDER BY COUNT (s.ip) DESC")
    List<ResponseStatsDto> findAllByTimeAndUris(LocalDateTime start, LocalDateTime end, List<String> uris);

    @Query("SELECT new ru.practicum.ResponseStatsDto(s.app, s.uri, COUNT (DISTINCT s.ip))" +
            "FROM Stats s " +
            "WHERE s.timestamp BETWEEN ?1 AND ?2 AND s.uri IN ?3 " +
            "GROUP BY s.app, s.uri " +
            "ORDER BY COUNT (DISTINCT s.ip) DESC")
    List<ResponseStatsDto> findAllByTimeAndUrisUnique(LocalDateTime start, LocalDateTime end, List<String> uris);
}
