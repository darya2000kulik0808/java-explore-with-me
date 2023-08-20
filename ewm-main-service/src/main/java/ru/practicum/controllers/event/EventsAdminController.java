package ru.practicum.controllers.event;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.event.EventFullDto;
import ru.practicum.dto.event.UpdateEventAdminRequest;
import ru.practicum.services.event.EventService;

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
@RequestMapping(path = "/admin/events")
public class EventsAdminController {

    private final EventService eventService;

    @GetMapping
    public List<EventFullDto> getAllEventsForAdmin(@RequestParam(required = false) List<Long> users,
                                                   @RequestParam(required = false) List<String> states,
                                                   @RequestParam(required = false) List<Long> categories,
                                                   @RequestParam(required = false)
                                                   @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
                                                   LocalDateTime rangeStart,
                                                   @RequestParam(required = false)
                                                   @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
                                                   LocalDateTime rangeEnd,
                                                   @PositiveOrZero(message = FROM_ERROR_MESSAGE)
                                                   @RequestParam(defaultValue = "0") Integer from,
                                                   @Positive(message = SIZE_ERROR_MESSAGE)
                                                   @RequestParam(defaultValue = "10") Integer size){
        log.info("Получили запрос на получение всех событий для админа.");
        return eventService.getAllForAdmin(users, states, categories, rangeStart, rangeEnd, from, size);
    }

    @PatchMapping("/{eventId}")
    public EventFullDto patchEventForAdmin(@PathVariable Long eventId,
                                            @RequestBody UpdateEventAdminRequest event){
        log.info("Получили запрос на редактирование ивента для админа.");
        return eventService.patchEventForAdmin(eventId, event);
    }
}
