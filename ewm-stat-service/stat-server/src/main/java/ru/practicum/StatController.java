package ru.practicum;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.service.StatService;

import javax.validation.Valid;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.Past;
import java.util.List;

@RestController
@AllArgsConstructor
@Slf4j
@Validated
public class StatController {

    StatService statService;

    @GetMapping("/stats")
    public List<ViewStatsDto> getStats(@Past @RequestParam String start, //yyyy-MM-dd HH:mm:ss
                                       @FutureOrPresent @RequestParam String end,    //yyyy-MM-dd HH:mm:ss
                                       @RequestParam(required = false, defaultValue = "") List<String> uris,
                                       @RequestParam(required = false, defaultValue = "false") Boolean unique) {
        log.info("Получили запрос на получение статистики запросов");
        return statService.getStats(start, end, uris, unique);
    }

    @PostMapping("/hit")
    @ResponseStatus(HttpStatus.CREATED)
    public String postStat(@RequestBody @Valid EndpointHitDto hit) {
        log.info("Получили запрос на добавление запроса к эндпоинту: {}, - сервиса: {}", hit.uri, hit.app);
        statService.postStat(hit);
        return "Информация сохранена";
    }
}
