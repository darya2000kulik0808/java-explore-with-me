package ru.practicum.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.EndpointHitDto;
import ru.practicum.ViewStatsDto;
import ru.practicum.mapper.EndpointHitsMapper;
import ru.practicum.model.EndpointHit;
import ru.practicum.repository.StatsRepository;

import javax.persistence.EntityManager;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;

@AllArgsConstructor
@Service
@Slf4j
public class StatServiceImpl implements StatService {

    StatsRepository statsRepository;

    EntityManager entityManager;

    public static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public void postStat(EndpointHitDto hit) {
        log.info("Перешли к записи запроса к эндпоинту в БД...");
        EndpointHit hit1 = statsRepository.save(EndpointHitsMapper.toEndPointHit(hit));
        log.info("Сохранили информацию о запросе в БД: {}", hit1);
    }

    @Override
    public List<ViewStatsDto> getStats(String start, String end,
                                       List<String> uris, Boolean unique) {
        log.info("Перешли к началу выборки статистики из БД...");
        LocalDateTime startTime = LocalDateTime.parse(URLDecoder.decode(start, StandardCharsets.UTF_8), formatter);
        LocalDateTime endTime = LocalDateTime.parse(URLDecoder.decode(end, StandardCharsets.UTF_8), formatter);
        List<ViewStatsDto> viewStats;
        if (!uris.isEmpty()) {
            log.info("Списк URI был передан.");
            viewStats = !unique ? statsRepository.findAllByTimestampBetweenAndUriIn(startTime, endTime, uris) :
                        statsRepository.findAllByTimestampBetweenAndUriInAndIpUnique(startTime, endTime, uris);
            if (viewStats.isEmpty()) {
                log.info("Пустой отчет для периода с {} по {}. Уникальные просмотры: {}.",
                        startTime, endTime, unique);
                return Collections.emptyList();
            }
            log.info("Сформировали статистику для периода с {} по {} для списка URI {}, уникальные просмотры: {}",
                    startTime, endTime, uris, unique);
        } else {
            log.info("Списк URI отсутствует.");
            viewStats = !unique ? statsRepository.findAllByTimestampBetween(startTime, endTime) :
                    statsRepository.findAllByTimestampBetweenWithUniqueIp(startTime, endTime);
            if (viewStats.isEmpty()) {
                log.info("Пустой отчет для периода с {} по {}. Уникальные просмотры: {}.",
                        startTime, endTime, unique);
                return Collections.emptyList();
            }
            log.info("Сформировали статистику для периода с {} по {}, уникальные просмотры: {}",
                    startTime, endTime, unique);
        }
        return viewStats;
    }
}
