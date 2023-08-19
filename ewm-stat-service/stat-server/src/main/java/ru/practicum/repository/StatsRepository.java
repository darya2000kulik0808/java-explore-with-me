package ru.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.ViewStatsDto;
import ru.practicum.model.EndpointHit;

import java.time.LocalDateTime;
import java.util.List;

public interface StatsRepository extends JpaRepository<EndpointHit, Integer> {

    @Query("select new ru.practicum.ViewStatsDto(h.app, h.uri, count(h.ip)) from EndpointHit h " +
            "where h.timestamp between :start and :end " +
            "group by  h.app, h.uri " +
            "order by h.app desc ")
    List<ViewStatsDto> findAllByTimestampBetween(@Param("start") LocalDateTime start,
                                                 @Param("end") LocalDateTime end);

    @Query("select new ru.practicum.ViewStatsDto(h.app, h.uri, count(h.ip)) from EndpointHit h " +
            "where h.timestamp between :start and :end " +
            "and h.uri in :uri  " +
            "group by  h.app, h.uri " +
            "order by h.app desc ")
    List<ViewStatsDto> findAllByTimestampBetweenAndUriIn(@Param("start") LocalDateTime start,
                                                         @Param("end") LocalDateTime end,
                                                         List<String> uri);

    @Query("select new ru.practicum.ViewStatsDto(h.app, h.uri, count(distinct h.ip)) from EndpointHit h " +
            "where h.timestamp between :start and :end " +
            "group by  h.app, h.uri " +
            "order by h.app desc ")
    List<ViewStatsDto> findAllByTimestampBetweenWithUniqueIp(@Param("start") LocalDateTime start,
                                                             @Param("end") LocalDateTime end);

    @Query("select new ru.practicum.ViewStatsDto(h.app, h.uri, count(distinct h.ip)) from EndpointHit h " +
            "where h.timestamp between :start and :end " +
            "and h.uri in :uri " +
            "group by  h.app, h.uri " +
            "order by h.app desc ")
    List<ViewStatsDto> findAllByTimestampBetweenAndUriInAndIpUnique(@Param("start") LocalDateTime start,
                                                                    @Param("end") LocalDateTime end,
                                                                    List<String> uri);
}
