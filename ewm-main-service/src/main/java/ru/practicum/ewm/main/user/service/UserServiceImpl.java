package ru.practicum.ewm.main.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.main.exception.NoSuchEntityException;
import ru.practicum.ewm.main.user.dto.NewUserRequest;
import ru.practicum.ewm.main.user.dto.UserDto;
import ru.practicum.ewm.main.user.mapper.UserMapper;
import ru.practicum.ewm.main.user.storage.UserStorage;

import javax.transaction.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserStorage userStorage;

    @Transactional
    @Override
    public UserDto addUser(NewUserRequest userRequest) {
        return UserMapper.toUserDto(userStorage.save(UserMapper.toUser(userRequest)));
    }

    @Transactional
    @Override
    public void deleteUser(long userId) {
        try {
            userStorage.deleteById(userId);
        } catch (EmptyResultDataAccessException e) {
            throw new NoSuchEntityException(String.format("No such user with id = %s", userId));
        }
    }

    @Transactional
    @Override
    public List<UserDto> getUserList(List<Long> ids, int from, int size) {
        if (!ids.isEmpty()) {
            return UserMapper.toUserDtoList(userStorage.findAllById(ids));
        } else {
            Pageable page = PageRequest.of(from / size, size, Sort.by(Sort.Direction.ASC, "id"));
            return UserMapper.toUserDtoList(userStorage.findAll(page).getContent());
        }
    }
}
