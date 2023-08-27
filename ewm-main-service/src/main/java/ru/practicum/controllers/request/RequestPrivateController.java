package ru.practicum.controllers.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.request.ParticipationRequestDto;
import ru.practicum.services.request.RequestService;

import javax.validation.constraints.NotNull;
import java.util.List;

@RestController
@RequestMapping("/users/{userId}/requests")
@Slf4j
@Validated
@RequiredArgsConstructor
public class RequestPrivateController {
    private final RequestService service;

    @GetMapping
    public List<ParticipationRequestDto> getAll(@PathVariable long userId) {
        return service.getAllByUserId(userId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ParticipationRequestDto add(@PathVariable Long userId,
                                       @NotNull(message = "Отсутствует id события в запросе")
                                       @RequestParam Long eventId) {
        return service.addRequest(userId, eventId);
    }

    @PatchMapping("/{requestId}/cancel")
    public ParticipationRequestDto cancel(@PathVariable Long userId,
                                          @PathVariable Long requestId) {
        return service.cancelRequest(userId, requestId);
    }
}
