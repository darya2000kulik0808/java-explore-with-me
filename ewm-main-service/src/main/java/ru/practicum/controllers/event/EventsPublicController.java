package ru.practicum.controllers.event;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.event.EventFullDto;
import ru.practicum.dto.event.EventShortDto;
import ru.practicum.services.event.EventService;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;
import java.util.List;

import static ru.practicum.errorHandler.ErrorMessages.FROM_ERROR_MESSAGE;
import static ru.practicum.errorHandler.ErrorMessages.SIZE_ERROR_MESSAGE;

@RestController
@AllArgsConstructor
@Slf4j
@Validated
@RequestMapping(path = "/events")
public class EventsPublicController {

    private final EventService eventService;

    @GetMapping
    public List<EventShortDto> getAllEventsPublic(@RequestParam(required = false) String text,
                                                  @RequestParam(required = false) List<Long> categories,
                                                  @RequestParam(required = false) Boolean paid,
                                                  @RequestParam(required = false)
                                                  @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
                                                  LocalDateTime rangeStart,
                                                  @RequestParam(required = false)
                                                  @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
                                                  LocalDateTime rangeEnd,
                                                  @RequestParam(defaultValue = "false") Boolean onlyAvailable,
                                                  @RequestParam(required = false, name = "sort") String sortParam,
                                                  @PositiveOrZero(message = FROM_ERROR_MESSAGE)
                                                  @RequestParam(defaultValue = "0") Integer from,
                                                  @Positive(message = SIZE_ERROR_MESSAGE)
                                                  @RequestParam(defaultValue = "10") Integer size,
                                                  HttpServletRequest request) {
        log.info("Запрос на получение всех событий с сокращенном виде.");
        return eventService.getAllPublic(text, categories, paid,
                                         rangeStart, rangeEnd, onlyAvailable,
                                         sortParam, from, size, request);
    }

    @GetMapping("/{eventId}")
    public EventFullDto getOneEventPublic(@PathVariable Long eventId,
                                          HttpServletRequest request) {
        log.info("Запрос на получение события по айди. ID : {}", eventId);
        return eventService.getOneEventPublic(eventId, request);
    }
}
