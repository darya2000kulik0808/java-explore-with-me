package ru.practicum.services.compilation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.compilation.CompilationDto;
import ru.practicum.dto.compilation.NewCompilationDto;
import ru.practicum.dto.compilation.UpdateCompilationRequest;
import ru.practicum.enums.CommentStatusEnum;
import ru.practicum.enums.RequestStatusEnum;
import ru.practicum.errorHandler.exceptions.ConflictException;
import ru.practicum.errorHandler.exceptions.ObjectNotFoundException;
import ru.practicum.mappers.CompilationMapper;
import ru.practicum.models.Compilation;
import ru.practicum.models.Event;
import ru.practicum.repositories.CommentRepository;
import ru.practicum.repositories.CompilationRepository;
import ru.practicum.repositories.EventRepository;
import ru.practicum.repositories.RequestRepository;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class CompilationServiceImpl implements CompilationService {
    private final CommentRepository commentRepository;

    private final CompilationRepository compilationRepository;
    private final EventRepository eventRepository;
    private final RequestRepository requestRepository;

    @Override
    @Transactional(readOnly = true)
    public List<CompilationDto> getAllCompilations(Boolean pinned, Integer from, Integer size) {
        PageRequest page = PageRequest.of(from / size, size);
        List<Compilation> compilationsFromDb;

        if (pinned != null) {
            compilationsFromDb = compilationRepository.findAllByPinned(pinned, page).getContent();
        } else {
            compilationsFromDb = compilationRepository.findAll(page).getContent();
        }

        if (compilationsFromDb.isEmpty()) {
            return Collections.emptyList();
        }

        List<CompilationDto> compilationDtos = compilationsFromDb.stream()
                .map(CompilationMapper::toCompilationDto)
                .collect(Collectors.toList());

        compilationDtos.forEach(
                compilationDto -> compilationDto.getEvents().forEach(
                        eventShortDto -> eventShortDto.setConfirmedRequests(
                                countConfirmedForEventShortDto(eventShortDto.getId())
                        )
                )
        );

        compilationDtos.forEach(
                compilationDto -> compilationDto.getEvents().forEach(
                        eventShortDto -> eventShortDto.setComments(
                                countCommentsForShortDto(eventShortDto.getId())
                        )
                )
        );
        return compilationDtos;
    }

    @Override
    @Transactional(readOnly = true)
    public CompilationDto getOneCompilation(Long compId) {
        Compilation compilation = compilationRepository.findById(compId).orElseThrow(
                () -> new ObjectNotFoundException(
                        String.format("Compilation with id=%d was not found", compId))
        );

        CompilationDto compilationDtos = CompilationMapper.toCompilationDto(compilation);

        compilationDtos.getEvents().forEach(eventShortDto -> eventShortDto.setConfirmedRequests(
                countConfirmedForEventShortDto(eventShortDto.getId())));
        compilationDtos.getEvents().forEach(eventShortDto -> eventShortDto.setComments(
                countCommentsForShortDto(eventShortDto.getId())));
        return compilationDtos;
    }

    @Override
    @Transactional
    public CompilationDto createNewCompilation(NewCompilationDto newCompilationDto) {
        if (newCompilationDto.getPinned() == null) {
            newCompilationDto.setPinned(false);
        }
        Compilation compilation = CompilationMapper.toCompilation(newCompilationDto);

        Compilation compilationByName = compilationRepository.findByTitle(newCompilationDto.getTitle());

        if (compilationByName != null) {
            throw new ConflictException(
                    String.format("Нарушение уникальности подборки. Подборка \"%s\" уже существует.",
                            compilationByName.getTitle()));
        }

        if (newCompilationDto.getEvents() != null) {
            Set<Event> events = eventRepository.findAllByIdIn(newCompilationDto.getEvents());
            compilation.setEvents(events);
        }

        compilation = compilationRepository.save(compilation);

        return CompilationMapper.toCompilationDto(compilation);
    }

    @Override
    @Transactional
    public CompilationDto patchCompilation(Long compId, UpdateCompilationRequest updateCompilationRequest) {
        Compilation compilation = compilationRepository.findById(compId).orElseThrow(
                () -> new ObjectNotFoundException(
                        String.format("Compilation with id=%d was not found", compId))
        );

        if (updateCompilationRequest.getPinned() != null) {
            compilation.setPinned(updateCompilationRequest.getPinned());
        }
        if (updateCompilationRequest.getTitle() != null) {
            Compilation compilationByName = compilationRepository.findByTitle(updateCompilationRequest.getTitle());

            if (compilationByName != null) {
                throw new ConflictException(
                        String.format("Нарушение уникальности подборки. Подборка \"%s\" уже существует.",
                                compilationByName.getTitle()));
            }
            compilation.setTitle(updateCompilationRequest.getTitle());
        }

        List<Long> eventIds = updateCompilationRequest.getEvents();
        if (eventIds != null && !eventIds.isEmpty()) {

            compilation.getEvents().clear();
            compilation = compilationRepository.save(compilation);

            Set<Event> events = eventRepository.findAllByIdIn(updateCompilationRequest.getEvents());
            compilation.setEvents(events);

            compilation = compilationRepository.save(compilation);
        }
        return CompilationMapper.toCompilationDto(compilation);
    }

    @Override
    @Transactional
    public void deleteCompilation(Long compId) {
        compilationRepository.findById(compId).orElseThrow(
                () -> new ObjectNotFoundException(
                        String.format("Compilation with id=%d was not found", compId))
        );

        compilationRepository.deleteById(compId);
    }

    private Integer countConfirmedForEventShortDto(Long eventId) {
        return requestRepository.countAllByStatusAndEvent_Id(RequestStatusEnum.CONFIRMED, eventId);
    }

    private Integer countCommentsForShortDto(Long eventId) {
        return commentRepository.countAllByEvent_IdAndStatus(eventId, CommentStatusEnum.PUBLISHED);
    }
}
