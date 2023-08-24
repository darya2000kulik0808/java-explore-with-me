package ru.practicum.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.enums.RequestStatusEnum;
import ru.practicum.models.Request;

import java.util.List;

public interface RequestRepository extends JpaRepository<Request, Long> {

    List<Request> findAllByRequester_Id(Long requestId);

    Integer countAllByStatusAndEvent_Id(RequestStatusEnum status, Long id);

    List<Request> findAllByEvent_Id(Long eventId);

    List<Request> findAllByIdIn(List<Long> ids);

    Request findByEvent_IdAndRequester_Id(Long eventId, Long requesterId);
}
