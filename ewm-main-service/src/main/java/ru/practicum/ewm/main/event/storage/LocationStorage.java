package ru.practicum.ewm.main.event.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.ewm.main.event.model.Location;

public interface LocationStorage extends JpaRepository<Location, Long> {
}
