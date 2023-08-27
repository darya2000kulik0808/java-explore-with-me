package ru.practicum.services.event;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ViewStatsDto;
import ru.practicum.dto.event.*;
import ru.practicum.enums.*;
import ru.practicum.errorHandler.exceptions.ConflictException;
import ru.practicum.errorHandler.exceptions.ObjectNotFoundException;
import ru.practicum.errorHandler.exceptions.StartTimeAndEndTimeException;
import ru.practicum.exceptions.StatsRequestException;
import ru.practicum.mappers.EventMapper;
import ru.practicum.mappers.LocationMapper;
import ru.practicum.models.Category;
import ru.practicum.models.Event;
import ru.practicum.models.Location;
import ru.practicum.models.User;
import ru.practicum.repositories.*;
import ru.practicum.services.statistics.StatisticRequestService;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {
    private static final String URI = "/events";
    private static final String APP = "ewm-main-service";

    private final EntityManager entityManager;
    private final RequestRepository requestRepository;
    private final EventRepository eventRepository;
    private final StatisticRequestService statsRequestService;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final LocationRepository locationRepository;
    private final CommentRepository commentRepository;

    @Override
    @Transactional(readOnly = true)
    public List<EventFullDto> getAllForAdmin(List<Long> users,
                                             List<String> states,
                                             List<Long> categories,
                                             LocalDateTime rangeStart,
                                             LocalDateTime rangeEnd,
                                             Integer from,
                                             Integer size) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Event> criteriaQuery = criteriaBuilder.createQuery(Event.class);
        Root<Event> root = criteriaQuery.from(Event.class);
        CriteriaQuery<Event> select = criteriaQuery.select(root);
        List<Predicate> predicates = new ArrayList<>();

        if (users != null) {
            Predicate inListOfUsers = root.get("initiator").in(users);
            predicates.add(inListOfUsers);
        }
        if (states != null) {
            List<EventStateEnum> stateEnums = states.stream()
                    .map(EventStateEnum::valueOf)
                    .collect(Collectors.toList());
            Predicate inListOfStates = root.get("state").in(stateEnums);
            predicates.add(inListOfStates);
        }
        if (categories != null) {
            Predicate inListOfCategoryId = root.get("category").in(categories);
            predicates.add(inListOfCategoryId);
        }

        List<Event> events = getEvents(rangeStart, rangeEnd, from, size, criteriaBuilder, root, select, predicates);

        return events
                .stream()
                .map(this::makeFullResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public EventFullDto patchEventForAdmin(Long eventId,
                                           UpdateEventAdminRequest updateEventAdminRequest) {
        Event event = eventRepository.findById(eventId).orElseThrow(
                () -> new ObjectNotFoundException(
                        String.format("Event with id=%d was not found", eventId))
        );

        checkNullable(event,
                updateEventAdminRequest.getAnnotation(),
                updateEventAdminRequest.getCategory(),
                updateEventAdminRequest.getDescription(),
                updateEventAdminRequest.getEventDate(),
                updateEventAdminRequest.getPaid(),
                updateEventAdminRequest.getParticipantLimit(),
                updateEventAdminRequest.getRequestModeration(),
                updateEventAdminRequest.getTitle());

        StateActionAdminEnum action;

        if (updateEventAdminRequest.getStateAction() != null) {
            String stateAction = updateEventAdminRequest.getStateAction().toString();
            if (event.getState() != EventStateEnum.PENDING) {
                throw new ConflictException("Событие можно опубликовать или отклонить только в состоянии PENDING");
            }

            try {
                action = StateActionAdminEnum.valueOf(stateAction);
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Unknown state: " + stateAction);
            }

            if (action == StateActionAdminEnum.PUBLISH_EVENT) {
                event.setState(EventStateEnum.PUBLISHED);
                event.setPublishedOn(LocalDateTime.now());
            } else {
                event.setState(EventStateEnum.CANCELED);
            }
        }
        event = eventRepository.save(event);
        return makeFullResponseDto(event);
    }

    //отправляет запрос к статистике
    @Override
    @Transactional(readOnly = true)
    public List<EventShortDto> getAllPublic(String text,
                                            List<Long> categories,
                                            Boolean paid,
                                            LocalDateTime rangeStart,
                                            LocalDateTime rangeEnd,
                                            Boolean onlyAvailable,
                                            String sortParam,
                                            Integer from,
                                            Integer size,
                                            HttpServletRequest request) {

        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Event> criteriaQuery = criteriaBuilder.createQuery(Event.class);
        Root<Event> root = criteriaQuery.from(Event.class);
        CriteriaQuery<Event> select = criteriaQuery.select(root);
        List<Predicate> predicates = new ArrayList<>();

        if (text != null) {
            text = text.toUpperCase();
            text = "%" + text + "%";
            Predicate byText = criteriaBuilder.like(criteriaBuilder.upper(root.get("annotation")), text);
            predicates.add(byText);
        }
        if (paid != null) {
            Predicate isPaid = criteriaBuilder.isTrue(root.get("paid"));
            predicates.add(isPaid);
        }
        if (categories != null) {
            List<Category> categoryList = categoryRepository.findAllByIdIn(categories);
            Predicate inListOfCategoryId = root.get("category").in(categoryList);
            predicates.add(inListOfCategoryId);
        }

        List<Event> events = getEvents(rangeStart, rangeEnd, from, size, criteriaBuilder, root, select, predicates);

        if (events.isEmpty()) {
            return Collections.emptyList();
        }

        List<EventFullDto> eventFullDtos = events
                .stream()
                .map(this::makeFullResponseDto)
                .collect(Collectors.toList());

        if (onlyAvailable) {
            eventFullDtos = eventFullDtos.stream()
                    .filter(event -> event.getConfirmedRequests() < event.getParticipantLimit())
                    .collect(Collectors.toList());
        }

        if (sortParam != null) {
            if (Objects.equals(sortParam.toUpperCase(), SortEventsEnum.VIEWS.toString())) {
                eventFullDtos = eventFullDtos.stream()
                        .sorted(Comparator.comparing(EventFullDto::getViews).reversed())
                        .collect(Collectors.toList());
            }
            if (Objects.equals(sortParam.toUpperCase(), SortEventsEnum.EVENT_DATE.toString())) {
                eventFullDtos = eventFullDtos.stream()
                        .sorted(Comparator.comparing(EventFullDto::getEventDate).reversed())
                        .collect(Collectors.toList());
            }
        }

        statsRequestService.addEndpointHit(URI, APP, request);

        return eventFullDtos.stream()
                .map(EventMapper::toEventShortDto)
                .collect(Collectors.toList());
    }

    //отправляет запрос к статистике
    @Override
    @Transactional(readOnly = true)
    public EventFullDto getOneEventPublic(Long eventId, HttpServletRequest request) {
        Event event = eventRepository.findById(eventId).orElseThrow(
                () -> new ObjectNotFoundException(
                        String.format("Event with id=%d was not found", eventId))
        );

        if (event.getState() != EventStateEnum.PUBLISHED) {
            throw new ObjectNotFoundException(String.format("Событие с id=%d еще не опубликовано", eventId));
        }

        String uri = URI + "/" + eventId;
        statsRequestService.addEndpointHit(uri, APP, request);

        EventFullDto eventFullDto = makeFullResponseDto(event);
        eventFullDto.setConfirmedRequests(countConfirmedForEventDtos(eventId));

        return eventFullDto;
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventFullDto> getByUserId(Long userId, Integer from, Integer size) {
        PageRequest page = PageRequest.of(from / size, size);
        List<Event> events = eventRepository.findAllByInitiator_Id(userId, page).getContent();

        if (events.isEmpty()) {
            return Collections.emptyList();
        }

        return events
                .stream()
                .map(this::makeFullResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public EventFullDto getUsersEventById(Long userId, Long eventId) {
        Event event = eventRepository.findByIdAndInitiator_Id(eventId, userId);
        return makeFullResponseDto(event);
    }

    @Override
    @Transactional
    public EventFullDto add(Long userId, NewEventDto newEventDto) {
        if (newEventDto.getRequestModeration() == null) {
            newEventDto.setRequestModeration(true);
        }
        if (newEventDto.getPaid() == null) {
            newEventDto.setPaid(false);
        }
        if (newEventDto.getParticipantLimit() == null) {
            newEventDto.setParticipantLimit(0);
        }

        Location locationFromDb = locationRepository.findByLatAndLon(newEventDto.getLocation().getLat(),
                newEventDto.getLocation().getLon());
        Location location;
        if (locationFromDb == null) {
            location = LocationMapper.toLocation(newEventDto.getLocation());
            location = locationRepository.save(location);
        } else {
            location = locationFromDb;
        }

        User user = userRepository.findById(userId).orElseThrow(
                () -> new ObjectNotFoundException(
                        String.format("User with id=%d was not found", userId)
                ));

        Category category = checkCategory(newEventDto.getCategory());

        Event event = EventMapper.toEvent(newEventDto);
        event.setInitiator(user);
        event.setLocation(location);
        event.setCreatedOn(LocalDateTime.now());
        event.setState(EventStateEnum.PENDING);
        event.setCategory(category);

        event = eventRepository.save(event);
        return makeFullResponseDto(event);
    }

    @Override
    @Transactional
    public EventFullDto update(Long userId, Long eventId, UpdateEventUserRequest updateEventUserRequest) {
        Event event = eventRepository.findById(eventId).orElseThrow(
                () -> new ObjectNotFoundException(
                        String.format("Event with id=%d was not found", eventId))
        );
        if (event.getState() == EventStateEnum.PUBLISHED) {
            throw new ConflictException("Only pending or canceled events can be changed");
        }
        userRepository.findById(userId).orElseThrow(
                () -> new ObjectNotFoundException(
                        String.format("User with id=%d was not found", userId)
                ));

        checkNullable(event,
                updateEventUserRequest.getAnnotation(),
                updateEventUserRequest.getCategory(),
                updateEventUserRequest.getDescription(),
                updateEventUserRequest.getEventDate(),
                updateEventUserRequest.getPaid(),
                updateEventUserRequest.getParticipantLimit(),
                updateEventUserRequest.getRequestModeration(),
                updateEventUserRequest.getTitle());

        if (updateEventUserRequest.getStateAction() != null) {
            String stateAction = updateEventUserRequest.getStateAction().toString();
            try {
                StateActionUserEnum.valueOf(stateAction);
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Unknown state: " + stateAction);
            }

            if (stateAction.equals(StateActionUserEnum.SEND_TO_REVIEW.toString())) {
                event.setState(EventStateEnum.PENDING);
            } else {
                event.setState(EventStateEnum.CANCELED);
            }
        }
        event = eventRepository.save(event);
        return makeFullResponseDto(event);
    }

    private List<Event> getEvents(LocalDateTime rangeStart,
                                  LocalDateTime rangeEnd,
                                  Integer from, Integer size,
                                  CriteriaBuilder criteriaBuilder,
                                  Root<Event> root,
                                  CriteriaQuery<Event> select,
                                  List<Predicate> predicates) {
        if (rangeEnd != null && rangeStart != null) {
            checkTime(rangeStart, rangeEnd);
            Predicate inTimeBetween = criteriaBuilder.between(root.get("eventDate"), rangeStart, rangeEnd);
            predicates.add(inTimeBetween);
        }

        TypedQuery<Event> typedQuery = entityManager.createQuery(select);

        if (!predicates.isEmpty()) {
            typedQuery = entityManager.createQuery(select.where(
                    predicates.toArray(new Predicate[predicates.size()])));
        }

        typedQuery.setFirstResult(from / size);
        typedQuery.setMaxResults(size);

        return typedQuery.getResultList();
    }

    private void checkNullable(Event event, String annotation,
                               Long category, String description,
                               LocalDateTime eventDate, Boolean paid,
                               Integer participantLimit, Boolean requestModeration,
                               String title) {
        if (annotation != null) {
            event.setAnnotation(annotation);
        }
        if (category != null) {
            Category category1 = checkCategory(category);
            event.setCategory(category1);
        }
        if (description != null) {
            event.setDescription(description);
        }
        if (eventDate != null) {
            event.setEventDate(eventDate);
        }
        if (paid != null) {
            event.setPaid(paid);
        }
        if (participantLimit != null) {
            event.setParticipantLimit(participantLimit);
        }
        if (requestModeration != null) {
            event.setRequestModeration(requestModeration);
        }
        if (title != null) {
            event.setTitle(title);
        }
    }

    private Category checkCategory(Long catId) {
        return categoryRepository.findById(catId)
                .orElseThrow(() -> new ObjectNotFoundException(
                        String.format("Category with id=%d was not found", catId)));
    }

    private Integer countConfirmedForEventDtos(Long eventId) {
        return requestRepository.countAllByStatusAndEvent_Id(RequestStatusEnum.CONFIRMED, eventId);
    }

    private EventFullDto makeFullResponseDto(Event event) {
        int confirmedRequests = countConfirmedForEventDtos(event.getId());
        long views = 0;
        if (event.getState() == EventStateEnum.PUBLISHED) {
            List<ViewStatsDto> viewStatsDto = statsRequestService.makeStatRequest(List.of(event));
            if (!viewStatsDto.isEmpty()) {
                long eventId = getEventId(viewStatsDto.get(0));
                if (event.getId() != eventId) {
                    throw new StatsRequestException(
                            String.format("Ошибка запроса статистики: запрошенный id %d не соответствует возвращенному %d",
                                    event.getId(), eventId)
                    );
                }
            }
            views = viewStatsDto.isEmpty() ? 0 : viewStatsDto.get(0).getHits();
        }

        Integer comments = getCommentsCount(event.getId());

        return EventMapper.toEventFullDto(event, confirmedRequests, views, comments);
    }

    public Integer getCommentsCount(Long eventId) {
        return commentRepository.countAllByEvent_IdAndStatus(eventId, CommentStatusEnum.PUBLISHED);
    }

    public static long getEventId(ViewStatsDto viewStatsDto) {
        StringTokenizer tokenizer = new StringTokenizer(viewStatsDto.getUri(), "/");
        if (!tokenizer.nextToken().equals("events")) {
            throw new StatsRequestException("Ошибка запроса статистики");
        }
        try {
            return Long.parseLong(tokenizer.nextToken());
        } catch (NumberFormatException e) {
            throw new StatsRequestException("Ошибка запроса данных статистики");
        }
    }

    private void checkTime(LocalDateTime start, LocalDateTime end) {
        if (start.equals(end)) {
            throw new StartTimeAndEndTimeException("Время начала не может совпадать с концом!");
        }
        if (start.isAfter(end)) {
            throw new StartTimeAndEndTimeException("Время начала не может быть позже конца!");

        }
    }
}
