package ru.practicum.user.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.user.dto.NewUserRequest;
import ru.practicum.user.dto.UserDto;
import ru.practicum.user.service.UserService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import java.util.List;

@Controller
@Validated
@Slf4j
@AllArgsConstructor
@RequestMapping("/admin/users")
public class UserAdminController {
    private static final String TAG = "UserAdmin CONTROLLER";
    private final UserService userService;

    @PostMapping
    public ResponseEntity<UserDto> createUser(@Valid @RequestBody NewUserRequest userRequest) {
        log.info("{} - Пришел запрос на добавление пользователем (POST /admin/users)", TAG);
        UserDto response = userService.createUser(userRequest);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<UserDto>> findUsers(@RequestParam(required = false) List<Long> ids,
                                                   @PositiveOrZero @RequestParam(defaultValue = "0") int from,
                                                   @Positive @RequestParam(defaultValue = "10") int size) {
        log.info("{} - Пришел запрос на получение списка всех пользователей GET /admin/users", TAG);
        List<UserDto> response = userService.findUsers(ids, from, size);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<String> deleteUser(@NotNull @PathVariable Long userId) {
        log.info("{} - Пришел запрос на удаление пользователя с id {} DELETE /admin/users/{userId}", TAG, userId);
        userService.deleteUser(userId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
