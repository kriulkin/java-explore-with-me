package ru.practicum.ewm.main.comment.storage;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.ewm.main.comment.dto.CommentDto;
import ru.practicum.ewm.main.comment.model.Comment;
import ru.practicum.ewm.main.event.model.Event;
import ru.practicum.ewm.main.user.model.User;

import java.util.List;

public interface CommentStorage extends JpaRepository<Comment, Long> {
    boolean existsByUserAndEvent(User user, Event event);

    List<Comment> findByUser(User user, Pageable page);

    List<Comment> findByEvent(Event event);

    List<CommentDto> findByEvent_Id(Long id);
}
