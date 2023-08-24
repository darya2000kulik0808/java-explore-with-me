package ru.practicum.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.models.Event;

import java.util.List;
import java.util.Set;

public interface EventRepository extends JpaRepository<Event, Long> {

    Integer countAllByCategory_Id(Long catId);

    Set<Event> findAllByIdIn(List<Long> ids);

    Page<Event> findAllByInitiator_Id(Long id, Pageable page);

    Event findByIdAndInitiator_Id(Long id, Long initId);
}
