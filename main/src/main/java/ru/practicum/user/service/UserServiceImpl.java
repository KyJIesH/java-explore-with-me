package ru.practicum.user.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.user.dto.NewUserRequest;
import ru.practicum.user.dto.UserDto;
import ru.practicum.user.mapper.UserMapper;
import ru.practicum.user.model.User;
import ru.practicum.user.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@AllArgsConstructor
public class UserServiceImpl implements UserService {

    private static final String TAG = "USER SERVICE";
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    @Transactional
    public UserDto createUser(NewUserRequest userRequest) {
        log.info("{} - Обработка запроса на добавление пользователя {}", TAG, userRequest);
        User response = userMapper.toUser(userRequest);

        try {
            response = userRepository.save(response);
        } catch (DataIntegrityViolationException e) {
            throw new ConflictException(e.getMessage(), e);
        }

        return userMapper.toUserDto(response);
    }

    @Override
    public User findUserById(Long userId) {
        log.info("{} - Обработка запроса на получение пользователя по id {}", TAG, userId);
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));
    }

    @Override
    public List<UserDto> findUsers(List<Long> ids, int from, int size) {
        log.info("{} - Обработка запроса на получение пользователей по ids {}", TAG, ids);
        List<User> response = new ArrayList<>();
        Pageable pageable = PageRequest.of(from / size, size);
        if (ids == null || ids.isEmpty()) {
            response = userRepository.findAll(pageable).getContent();
        } else {
            response = userRepository.findByIdIn(ids, pageable);
        }
        return userMapper.toUserDtos(response);
    }

    @Override
    @Transactional
    public void deleteUser(Long userId) {
        log.info("{} - Обработка запроса на удаление пользователя по id {}", TAG, userId);
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        userRepository.deleteById(userId);
    }
}
