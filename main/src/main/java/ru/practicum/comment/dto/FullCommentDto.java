package ru.practicum.comment.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class FullCommentDto {
    private Long id;
    @NotBlank
    @Size(min = 1, max = 5000)
    private String text;
    @NotNull
    private Long authorId;
    @NotNull
    private Long eventId;
    @NotBlank
    private String created;
    @NotBlank
    private String updated;
}
