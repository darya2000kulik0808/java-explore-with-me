package ru.practicum.controllers.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.requests.ParticipationRequestDto;
import ru.practicum.dto.event.*;
import ru.practicum.dto.requests.EventRequestStatusUpdateRequest;
import ru.practicum.dto.requests.EventRequestStatusUpdateResult;
import ru.practicum.services.event.EventService;
import ru.practicum.services.request.RequestService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

import static ru.practicum.errorHandler.ErrorMessages.FROM_ERROR_MESSAGE;
import static ru.practicum.errorHandler.ErrorMessages.SIZE_ERROR_MESSAGE;

@RestController
@RequestMapping("/users/{userId}/events")
@Slf4j
@Validated
@RequiredArgsConstructor
public class EventsPrivateController {
    private final EventService service;
    private final RequestService requestService;

    @GetMapping
    public List<EventFullDto> getByUserId(@PathVariable long userId,
                                          @PositiveOrZero(message = FROM_ERROR_MESSAGE)
                                          @RequestParam(defaultValue = "0") Integer from,
                                          @Positive(message = SIZE_ERROR_MESSAGE)
                                          @RequestParam(defaultValue = "10") Integer size) {
        return service.getByUserId(userId, from, size);
    }

    @GetMapping("/{eventId}")
    public EventFullDto getUsersEventById(@PathVariable long userId,
                                          @PathVariable long eventId) {
        return service.getUsersEventById(userId, eventId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EventFullDto add(@PathVariable long userId,
                            @Valid @RequestBody NewEventDto newEventDto) {
        return service.add(userId, newEventDto);
    }

    @PatchMapping("/{eventId}")
    public EventFullDto update(@PathVariable long userId,
                               @PathVariable long eventId,
                               @Valid @RequestBody UpdateEventUserRequest updateEvent) {

        return service.update(userId, eventId, updateEvent);
    }

    @GetMapping("/{eventId}/requests")
    public List<ParticipationRequestDto> getRequestsForUsersEvent(@PathVariable long userId,
                                                                  @PathVariable long eventId) {
        return requestService.getRequestsForUsersEvent(userId, eventId);
    }

    @PatchMapping("/{eventId}/requests")
    public EventRequestStatusUpdateResult updateRequests(@PathVariable long userId,
                                                         @PathVariable long eventId,
                                                         @Valid @RequestBody EventRequestStatusUpdateRequest
                                                                 eventRequestStatusUpdateRequest) {
        return requestService.updateRequests(userId, eventId, eventRequestStatusUpdateRequest);
    }
}
