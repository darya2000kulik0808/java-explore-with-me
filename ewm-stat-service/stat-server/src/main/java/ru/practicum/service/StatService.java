package ru.practicum.service;

import ru.practicum.EndpointHitDto;
import ru.practicum.ViewStatsDto;

import java.util.List;

public interface StatService {

    void postStat(EndpointHitDto hit);

    List<ViewStatsDto> getStats(String start, String end, List<String> uris, Boolean unique);
}
