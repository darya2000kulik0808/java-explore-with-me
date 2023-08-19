package ru.practicum;

import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import javax.validation.constraints.PastOrPresent;
import java.time.LocalDateTime;
import java.util.List;

@Validated
public interface StatClient {

    void addEndpointHit(@Valid EndpointHitDto endpointHitDto);

    //getStats with only end and start time
    List<ViewStatsDto> getStats(@NotNull(message = "Отсутствует дата начала!")
                                @Past(message = "Дата начала должна быть в прошлом!")
                                LocalDateTime start,
                                @NotNull(message = "Дата конца не может быть пустой!")
                                @PastOrPresent(message = "Дата конца не может быть в будущем!")
                                LocalDateTime end);

    //getStats with end/start time and unique ips
    List<ViewStatsDto> getStats(@NotNull(message = "Отсутствует дата начала!")
                                @Past(message = "Дата начала должна быть в прошлом!")
                                LocalDateTime start,
                                @NotNull(message = "Дата конца не может быть пустой!")
                                @PastOrPresent(message = "Дата конца не может быть в будущем!")
                                LocalDateTime end,
                                @NotNull(message = "Отсутствует флаг уникальности ip!")
                                Boolean unique);

    //getStats with end/start time and list of uris
    List<ViewStatsDto> getStats(@NotNull(message = "Отсутствует дата начала!")
                                @Past(message = "Дата начала должна быть в прошлом!")
                                LocalDateTime start,
                                @NotNull(message = "Дата конца не может быть пустой!")
                                @PastOrPresent(message = "Дата конца не может быть в будущем!")
                                LocalDateTime end,
                                @NotNull(message = "Отсутствует список uri!")
                                List<String> uris);

    //getStats with end/start time and unique ips and list of uris
    List<ViewStatsDto> getStats(@NotNull(message = "Отсутствует дата начала!")
                                @Past(message = "Дата начала должна быть в прошлом!")
                                LocalDateTime start,
                                @NotNull(message = "Дата конца не может быть пустой!")
                                @PastOrPresent(message = "Дата конца не может быть в будущем!")
                                LocalDateTime end,
                                @NotNull(message = "Отсутствует список uri!")
                                List<String> uris,
                                @NotNull(message = "Отсутствует флаг уникальности ip!")
                                Boolean unique);
}