package ru.practicum.ewm.main.user.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.ewm.main.user.model.User;

public interface UserStorage extends JpaRepository<User, Long> {
}
