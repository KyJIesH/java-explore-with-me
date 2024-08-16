package ru.practicum.user.service;


import ru.practicum.user.dto.NewUserRequest;
import ru.practicum.user.dto.UserDto;
import ru.practicum.user.model.User;

import java.util.List;

public interface UserService {

    UserDto createUser(NewUserRequest userRequest);

    User findUserById(Long userId);

    List<UserDto> findUsers(List<Long> ids, int from, int size);

    void deleteUser(Long userId);
}
