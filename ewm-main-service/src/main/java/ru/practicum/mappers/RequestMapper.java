package ru.practicum.mappers;

import ru.practicum.dto.requests.EventRequestStatusUpdateResult;
import ru.practicum.dto.requests.ParticipationRequestDto;
import ru.practicum.models.Request;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class RequestMapper {

    public static ParticipationRequestDto toParticipationRequestDto(Request request) {
        return ParticipationRequestDto.builder()
                .id(request.getId())
                .requester(request.getRequester().getId())
                .event(request.getEvent().getId())
                .created(request.getCreated())
                .status(request.getStatus().toString())
                .build();
    }

    public static EventRequestStatusUpdateResult toEventRequestStatusUpdateResult(List<Request> confirmed,
                                                                                  List<Request> rejected) {
        List<ParticipationRequestDto> confirmedRequests;
        List<ParticipationRequestDto> rejectedRequests;
        if (confirmed.isEmpty()) {
            confirmedRequests = Collections.emptyList();
        } else {
            confirmedRequests = confirmed.stream()
                    .map(RequestMapper::toParticipationRequestDto)
                    .collect(Collectors.toList());
        }
        if (rejected.isEmpty()) {
            rejectedRequests = Collections.emptyList();
        } else {
            rejectedRequests = rejected.stream()
                    .map(RequestMapper::toParticipationRequestDto)
                    .collect(Collectors.toList());
        }
        return new EventRequestStatusUpdateResult(confirmedRequests, rejectedRequests);
    }
}
