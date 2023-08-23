package ru.practicum.controllers.compilation;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.compilation.CompilationDto;
import ru.practicum.dto.compilation.NewCompilationDto;
import ru.practicum.dto.compilation.UpdateCompilationRequest;
import ru.practicum.services.compilation.CompilationService;

import javax.validation.Valid;

@RestController
@AllArgsConstructor
@Slf4j
@Validated
@RequestMapping(path = "/admin/compilations")
public class CompilationsAdminController {

    private final CompilationService compilationService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CompilationDto createNewCompilation(@Valid @RequestBody NewCompilationDto compilation) {
        log.info("Получили запрос на создание подборки.");
        return compilationService.createNewCompilation(compilation);
    }

    @PatchMapping("/{compId}")
    public CompilationDto patchCompilation(@PathVariable Long compId,
                                           @Valid @RequestBody UpdateCompilationRequest updateCompilationRequest) {
        log.info("Получили запрос на изменение подборки.");
        return compilationService.patchCompilation(compId, updateCompilationRequest);
    }

    @DeleteMapping("/{compId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCompilation(@PathVariable Long compId) {
        log.info("Получили запрос на удаление подборки.");
        compilationService.deleteCompilation(compId);
    }
}
