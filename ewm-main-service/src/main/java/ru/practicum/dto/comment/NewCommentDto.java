package ru.practicum.dto.comment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NewCommentDto {
    @NotBlank(message = "Текст комментария не может быть пустым!")
    @Size(max = 1000, min = 20, message = "Длина строки не может превышать 1000 символов и не может быть меньше 20 символов!")
    private String text;
}