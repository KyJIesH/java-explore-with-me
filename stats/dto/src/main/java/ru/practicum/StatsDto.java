package ru.practicum;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class StatsDto {
    private Long id;
    @NotEmpty(message = "Идентификатор сервиса для которого записывается информация должен быть заполнен")
    private String app;
    @NotEmpty(message = "URI для которого был осуществлен запрос должен быть заполнен")
    private String uri;
    @NotEmpty(message = "IP-адрес пользователя, осуществившего запрос должен быть заполнен")
    private String ip;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @NotNull(message = "Дата и время, когда был совершен запрос к эндпоинту " +
            "(должна быть заполнена в формате \"yyyy-MM-dd HH:mm:ss\")")
    private LocalDateTime timestamp;
}
