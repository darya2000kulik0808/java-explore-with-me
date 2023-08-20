package ru.practicum.services.event;

import ru.practicum.dto.event.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;

public interface EventService {
    List<EventFullDto> getAllForAdmin(List<Long> users, List<String> states, List<Long> categories, LocalDateTime rangeStart, LocalDateTime rangeEnd, Integer from, Integer size);

    EventFullDto patchEventForAdmin(Long eventId, UpdateEventAdminRequest event);

    List<EventShortDto> getAllPublic(String text, List<Long> categories, Boolean paid, LocalDateTime rangeStart, LocalDateTime rangeEnd, Boolean onlyAvailable, String sortParam, Integer from, Integer size, HttpServletRequest request);

    EventFullDto getOneEventPublic(Long eventId, HttpServletRequest request);

    List<EventFullDto> getByUserId(long userId, Integer from, Integer size);

    EventFullDto getUsersEventById(long userId, long eventId);

    EventFullDto add(long userId, NewEventDto newEventDto);

    EventFullDto update(long userId, long eventId, UpdateEventUserRequest updateEvent);
}
