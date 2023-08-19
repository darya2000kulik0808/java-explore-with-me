package ru.practicum.mapper;

import ru.practicum.EndpointHitDto;
import ru.practicum.model.EndpointHit;

public class EndpointHitsMapper {

    public static EndpointHitDto toEndPointHitDto(EndpointHit hit) {
        EndpointHitDto endpointHitDto = new EndpointHitDto();
        endpointHitDto.setApp(hit.getApp());
        endpointHitDto.setIp(hit.getIp());
        endpointHitDto.setUri(hit.getUri());
        endpointHitDto.setTimestamp(hit.getTimestamp());

        return endpointHitDto;
    }

    public static EndpointHit toEndPointHit(EndpointHitDto hit) {
        EndpointHit endpointHit = new EndpointHit();
        endpointHit.setApp(hit.getApp());
        endpointHit.setIp(hit.getIp());
        endpointHit.setUri(hit.getUri());
        endpointHit.setTimestamp(hit.getTimestamp());

        return endpointHit;
    }

}
