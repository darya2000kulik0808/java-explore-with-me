package ru.practicum.dto.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NewUserRequest {
    @NotBlank(message = "Электронная почта не может быть пустой!")
    @Size(max = 254, min = 6, message = "Длина строки не может превышать 254 символа и не может быть меньше 6 символов!")
    @Email(message = "Строка не является электронной почтой!")
    private String email;
    @NotBlank(message = "Имя не может быть пустым!")
    @Size(max = 250, min = 2, message = "Длина строки не может превышать 250 символов и не может быть меньше 2 символов!")
    private String name;
}
