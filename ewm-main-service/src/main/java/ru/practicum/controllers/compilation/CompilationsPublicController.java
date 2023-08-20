package ru.practicum.controllers.compilation;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.compilation.CompilationDto;
import ru.practicum.services.compilation.CompilationService;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

import static ru.practicum.errorHandler.ErrorMessages.FROM_ERROR_MESSAGE;
import static ru.practicum.errorHandler.ErrorMessages.SIZE_ERROR_MESSAGE;

@RestController
@AllArgsConstructor
@Slf4j
@Validated
@RequestMapping(path = "/compilations")
public class CompilationsPublicController {
    private final CompilationService compilationService;

    @GetMapping
    public List<CompilationDto> getAllCompilations(@RequestParam(required = false) Boolean pinned,
                                                   @PositiveOrZero(message = FROM_ERROR_MESSAGE)
                                                   @RequestParam(defaultValue = "0") Integer from,
                                                   @Positive(message = SIZE_ERROR_MESSAGE)
                                                   @RequestParam(defaultValue = "10") Integer size){
        log.info("Получили запрос на получение всех подборок.");
        return compilationService.getAllCompilations(pinned, from, size);
    }

    @GetMapping("/{compId}")
    public CompilationDto getOneCompilation(@PathVariable Long compId){
        log.info("Получили запрос на получение подпорки с id {}", compId);
        return compilationService.getOneCompilation(compId);
    }
}
