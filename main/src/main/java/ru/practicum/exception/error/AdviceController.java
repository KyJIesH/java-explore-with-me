package ru.practicum.exception.error;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.exception.BadRequestException;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.exception.ValidationException;

import jakarta.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.LocalDateTime;
import java.util.Collections;

@Slf4j
@RestControllerAdvice
public class AdviceController {
    private static final String TAG = "ADVICE CONTROLLER";

    @ExceptionHandler(value = ConflictException.class)
    @ResponseStatus(value = HttpStatus.CONFLICT)
    public ApiError handleConflictException(ConflictException e, HttpServletResponse response) {
        log.error("{} ConflictException", TAG, e);
        response.setStatus(HttpStatus.CONFLICT.value());
        StringWriter out = new StringWriter();
        e.printStackTrace(new PrintWriter(out));
        String stackTrace = out.toString();

        return new ApiError(
                HttpStatus.CONFLICT,
                "Некорректный запрос",
                e.getMessage(),
                LocalDateTime.now(),
                Collections.singletonList(stackTrace));
    }

    @ExceptionHandler(value = BadRequestException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public ApiError handleBadRequestException(BadRequestException e) {
        log.error("{} BadRequestException", TAG, e);

        StringWriter out = new StringWriter();
        e.printStackTrace(new PrintWriter(out));
        String stackTrace = out.toString();

        return new ApiError(
                HttpStatus.BAD_REQUEST,
                "Ошибочные данные в запросе",
                e.getMessage(),
                LocalDateTime.now(),
                Collections.singletonList(stackTrace));
    }

    @ExceptionHandler(value = NotFoundException.class)
    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    public ApiError handleBadRequestException(NotFoundException e) {
        log.error("{} NotFoundException", TAG, e);

        StringWriter out = new StringWriter();
        e.printStackTrace(new PrintWriter(out));
        String stackTrace = out.toString();

        return new ApiError(
                HttpStatus.NOT_FOUND,
                "Данные не найдены",
                e.getMessage(),
                LocalDateTime.now(),
                Collections.singletonList(stackTrace));
    }

    @ExceptionHandler(value = ValidationException.class)
    @ResponseStatus(value = HttpStatus.CONFLICT)
    public ApiError handleBadRequestException(ValidationException e) {
        log.error("{} ValidationException", TAG, e);

        StringWriter out = new StringWriter();
        e.printStackTrace(new PrintWriter(out));
        String stackTrace = out.toString();

        return new ApiError(
                HttpStatus.CONFLICT,
                "Ошибка валидации данных",
                e.getMessage(),
                LocalDateTime.now(),
                Collections.singletonList(stackTrace));
    }
}
