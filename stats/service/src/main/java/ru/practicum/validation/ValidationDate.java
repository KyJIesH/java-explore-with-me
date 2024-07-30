package ru.practicum.validation;

import ru.practicum.RequestStatsDto;
import ru.practicum.exception.ValidationException;


public class ValidationDate implements Validation {
    @Override
    public void validationDate(RequestStatsDto requestStatsDto) {
        if (requestStatsDto.getStart() == null || requestStatsDto.getEnd() == null) {
            throw new ValidationException("Дата не может быть null");
        }

        if (requestStatsDto.getStart().isAfter(requestStatsDto.getEnd())) {
            throw new ValidationException("Дата начала не может быть позже даты окончания");
        }
    }
}
