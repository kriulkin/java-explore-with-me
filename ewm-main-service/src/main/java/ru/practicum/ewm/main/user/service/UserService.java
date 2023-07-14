package ru.practicum.ewm.main.user.service;

import ru.practicum.ewm.main.user.dto.NewUserRequest;
import ru.practicum.ewm.main.user.dto.UserDto;

import java.util.List;

public interface UserService {
    UserDto addUser(NewUserRequest userRequest);

    void deleteUser(long userId);

    List<UserDto> getUserList(List<Long> ids, int from, int size);
}
