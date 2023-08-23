package ru.practicum.services.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.requests.EventRequestStatusUpdateRequest;
import ru.practicum.dto.requests.EventRequestStatusUpdateResult;
import ru.practicum.dto.requests.ParticipationRequestDto;
import ru.practicum.enums.EventStateEnum;
import ru.practicum.enums.RequestStatusEnum;
import ru.practicum.errorHandler.exceptions.ConflictException;
import ru.practicum.errorHandler.exceptions.ObjectNotFoundException;
import ru.practicum.mappers.RequestMapper;
import ru.practicum.models.Event;
import ru.practicum.models.Request;
import ru.practicum.models.User;
import ru.practicum.repositories.EventRepository;
import ru.practicum.repositories.RequestRepository;
import ru.practicum.repositories.UserRepository;

import javax.validation.ValidationException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@Slf4j
@RequiredArgsConstructor
public class RequestServiceImpl implements RequestService {

    private final RequestRepository requestRepository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public List<ParticipationRequestDto> getAllByUserId(Long userId) {
        checkUser(userId);
        List<Request> requests = requestRepository.findAllByRequester_Id(userId);
        if (requests.isEmpty()) {
            return Collections.emptyList();
        }
        return requests
                .stream()
                .map(RequestMapper::toParticipationRequestDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ParticipationRequestDto addRequest(Long userId, Long eventId) {
        Event event = checkEvent(eventId);
        User user = checkUser(userId);

        if (!event.getState().equals(EventStateEnum.PUBLISHED)) {
            log.warn("Попытка запроса на участие в неопублкованном событии с id {} от пользователя с id {}",
                    eventId, userId);
            throw new ConflictException(String.format(
                    "Нельзя добавить запрос на участие в неопублкованном событии  с id=%d", eventId));
        }

        if (event.getInitiator().getId().equals(userId)) {
            log.warn("Попытка запроса на участие в событии с id {} от инициатора с id {}",
                    eventId, userId);
            throw new ConflictException(String.format(
                    "Нельзя добавить запрос на участие в событии с id=%d для инициатора", eventId));
        }

        Request requestByUserIdAndEventId = requestRepository.findByEvent_IdAndRequester_Id(eventId, userId);
        if (requestByUserIdAndEventId != null) {
            String error = String.format("Нельзя добавить повторный запрос на участие событии с id %d", eventId);
            log.warn("Попытка повторного запроса на участие в событии с id {} от пользователя с id {}",
                    eventId, userId);
            throw new ConflictException(error);
        }

        int confirmedRequests = requestRepository
                .countAllByStatusAndEvent_Id(RequestStatusEnum.CONFIRMED, eventId);
        int limit = event.getParticipantLimit();
        if (limit != 0 && confirmedRequests >= limit) {
            throw new ConflictException(
                    String.format("У события с id=%d достигнут лимит запросов на участие: %d",
                            eventId, event.getParticipantLimit()));
        }

        Request request = Request.builder()
                .requester(user)
                .created(LocalDateTime.now())
                .event(event)
                .build();

        if (!event.getRequestModeration() && (confirmedRequests < limit) || limit == 0) {
            request.setStatus(RequestStatusEnum.CONFIRMED);
        } else {
            request.setStatus(RequestStatusEnum.PENDING);
        }

        return RequestMapper.toParticipationRequestDto(requestRepository.save(request));
    }

    @Override
    @Transactional
    public ParticipationRequestDto cancelRequest(Long userId, Long requestId) {
        checkUser(userId);
        Request request = requestRepository.findById(requestId).orElseThrow(
                () -> new ObjectNotFoundException(
                        String.format("Request with id=%d was not found", requestId))
        );

        if (!Objects.equals(request.getRequester().getId(), userId)) {
            log.warn("Редактирование запроса с id {} недоступно для пользователя с id {}", requestId, userId);
            throw new ObjectNotFoundException(
                    String.format("Редактирование запроса с id %d недоступно для пользователя с id %d",
                            requestId, userId));
        }

        request.setStatus(RequestStatusEnum.CANCELED);
        request = requestRepository.save(request);

        return RequestMapper.toParticipationRequestDto(request);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ParticipationRequestDto> getRequestsForUsersEvent(Long userId, Long eventId) {
        Event event = checkEvent(eventId);
        if (!event.getInitiator().getId().equals(userId)) {
            log.warn("Пользователь с id {} не организатор события с id {}", userId, eventId);
            throw new ValidationException(
                    String.format("Пользователь с id %d не организатор события с id %d", userId, eventId));
        }
        List<Request> requests = requestRepository.findAllByEvent_Id(eventId);
        return requests.stream()
                .map(RequestMapper::toParticipationRequestDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public EventRequestStatusUpdateResult updateRequests(Long userId, Long eventId, EventRequestStatusUpdateRequest eventRequestStatusUpdateRequest) {
        String statusParam = eventRequestStatusUpdateRequest.getStatus();
        RequestStatusEnum newStatus = checkStatus(statusParam);

        Event event = checkEvent(eventId);

        int participantLimit = event.getParticipantLimit();
        if (participantLimit == 0 || !event.getRequestModeration()) {
            log.warn("Событие с id {} не требует одобрения заявок", event.getId());
            throw new ConflictException(String.format("Событие с id %d не требует одобрения заявок", event.getId()));
        }

        List<Long> requestIds = eventRequestStatusUpdateRequest.getRequestIds();
        List<Request> requestsToUpdate = requestRepository.findAllByIdIn(requestIds);

        requestsToUpdate.forEach(request -> {
            if (!request.getEvent().getId().equals(eventId)) {
                log.warn("Заявка с id {} не относится к событию с id {}", request.getId(), eventId);
                throw new ObjectNotFoundException(
                        String.format("Заявка с id %d не относится к событию с id %d", request.getId(), eventId));
            }
            if (request.getStatus() != RequestStatusEnum.PENDING) {
                log.warn("Попытка изменить статус у заявок не в состоянии ожидания");
                throw new ConflictException("Можно изменить статус только у заявок, находящихся в состоянии ожидания");
            }
        });

        switch (newStatus) {
            case CONFIRMED:
                int countConfirmedInEvent = requestRepository.countAllByStatusAndEvent_Id(RequestStatusEnum.CONFIRMED, eventId);
                if (countConfirmedInEvent >= event.getParticipantLimit()) {
                    throw new ConflictException(
                            String.format("У события с id=%d достигнут лимит запросов на участие: %d",
                                    eventId, event.getParticipantLimit()));
                }

                List<Request> confirmedRequests;
                List<Request> rejectedRequests;

                List<Request> confirmedInTheEnd;
                List<Request> rejectedInTheEnd = new ArrayList<>();

                int requestsAmount = requestsToUpdate.size();
                int freeToConfirm = participantLimit - countConfirmedInEvent;

                if (freeToConfirm >= requestsAmount) {
                    requestsToUpdate.forEach(request -> request.setStatus(RequestStatusEnum.CONFIRMED));
                    confirmedInTheEnd = requestRepository.saveAll(requestsToUpdate);
                } else {
                    IntStream.range(0, freeToConfirm)
                            .forEach(i -> requestsToUpdate.get(i).setStatus(RequestStatusEnum.CONFIRMED));
                    IntStream.range(freeToConfirm, requestsAmount)
                            .forEach(i -> requestsToUpdate.get(i).setStatus(RequestStatusEnum.REJECTED));

                    confirmedRequests = requestsToUpdate.stream().limit(freeToConfirm).collect(Collectors.toList());
                    rejectedRequests = requestsToUpdate.stream().skip(freeToConfirm).collect(Collectors.toList());

                    confirmedInTheEnd = requestRepository.saveAll(confirmedRequests);
                    rejectedInTheEnd = requestRepository.saveAll(rejectedRequests);
                }
                return RequestMapper.toEventRequestStatusUpdateResult(confirmedInTheEnd, rejectedInTheEnd);
            case REJECTED:
                requestsToUpdate.forEach(request -> request.setStatus(RequestStatusEnum.REJECTED));
                List<Request> requests = requestRepository.saveAll(requestsToUpdate);
                return RequestMapper.toEventRequestStatusUpdateResult(Collections.emptyList(), requests);
            case PENDING:
                log.warn("Нельзя изменить статус на \"PENDING\"");
                throw new ValidationException("Нельзя изменить статус на \"PENDING\"");
            default:
                log.warn("Нельзя изменить статус на \"CANCELED\"");
                throw new ValidationException("Нельзя изменить статус на \"CANCELED\"");
        }
    }

    private User checkUser(Long userId) {
        return userRepository.findById(userId).orElseThrow(
                () -> new ObjectNotFoundException(
                        String.format("User with id=%d was not found", userId))
        );
    }

    private Event checkEvent(Long eventId) {
        return eventRepository.findById(eventId).orElseThrow(
                () -> new ObjectNotFoundException(
                        String.format("Event with id=%d was not found", eventId))
        );
    }

    private RequestStatusEnum checkStatus(String status) {
        try {
            return RequestStatusEnum.valueOf(status);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Unknown status: " + status);
        }
    }
}
