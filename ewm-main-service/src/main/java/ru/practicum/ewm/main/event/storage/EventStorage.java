package ru.practicum.ewm.main.event.storage;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import ru.practicum.ewm.main.event.model.Event;
import ru.practicum.ewm.main.user.model.User;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface EventStorage extends JpaRepository<Event, Long>, QuerydslPredicateExecutor<Event> {
    List<Event> findByIdIn(List<Long> events);

    Optional<Event> findByIdAndInitiator(long eventId, User user);

    Page<Event> findByInitiator(User user, Pageable page);

    Set<Event> findByIdIn(Set<Long> events);
}
