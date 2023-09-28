package ru.practicum.shareit.user.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
public class UserDto {
    private Integer id;
    @NotBlank(message = "Имя пользователя не может быть пустым")
    private final String name;
    @NotNull(message = "Email пользователя не может быть пустым")
    @Email(message = "Email пользователя должен быть корректным")
    private final String email;
}
