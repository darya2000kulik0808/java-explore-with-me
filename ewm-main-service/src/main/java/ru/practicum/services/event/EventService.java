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

    List<EventFullDto> getByUserId(Long userId, Integer from, Integer size);

    EventFullDto getUsersEventById(Long userId, Long eventId);

    EventFullDto add(Long userId, NewEventDto newEventDto);

    EventFullDto update(Long userId, Long eventId, UpdateEventUserRequest updateEvent);
}
