package ru.practicum.services.statistics;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.StatClient;
import ru.practicum.ViewStatsDto;
import ru.practicum.enums.EventStateEnum;
import ru.practicum.models.Event;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StatisticRequestService {

    private static final String URI = "events";
    private final StatClient client;

    public List<ViewStatsDto> makeStatRequest(List<Event> events) {
        if (events.stream().noneMatch(event -> event.getState() == EventStateEnum.PUBLISHED)) {
            return Collections.emptyList();
        }
        List<Event> eventsPublished = events.stream()
                .filter(event -> event.getState() == EventStateEnum.PUBLISHED)
                .collect(Collectors.toList());
        List<Long> eventIds = events.stream().map(Event::getId).collect(Collectors.toList());
        List<String> uris = eventIds.stream().map(id -> "/" + URI + "/" + id).collect(Collectors.toList());
        LocalDateTime startStat = eventsPublished.stream()
                .map(Event::getPublishedOn)
                .filter(Objects::nonNull)
                .sorted().collect(Collectors.toList()).get(0);
        boolean unique = true;
        return client.getStats(startStat.minusHours(1), LocalDateTime.now(), uris, unique);
    }
}
