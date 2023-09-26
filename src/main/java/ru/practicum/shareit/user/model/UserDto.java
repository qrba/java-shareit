package ru.practicum.shareit.user.model;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class UserDto {
    private final Integer id;
    @NotBlank(message = "Имя пользователя не может быть пустым")
    private final String name;
    @NotNull(message = "Email пользователя не может быть пустым")
    @Email(message = "Email пользователя должен быть корректным")
    private final String email;
}
