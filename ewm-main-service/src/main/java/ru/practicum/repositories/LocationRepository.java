package ru.practicum.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.models.Location;

public interface LocationRepository extends JpaRepository<Location, Long> {

    Location findByLatAndLon(Float lat, Float lon);
}
