package ru.practicum.shareit.item.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class ItemDto {
    private final Integer id;
    @NotBlank(message = "Имя вещи не может быть пустым")
    private final String name;
    @NotBlank(message = "Описание вещи не может быть пустым")
    private final String description;
    @JsonProperty("available")
    @NotNull(message = "Должна быть указана доступность вещи")
    private final Boolean isAvailable;
    private final Integer requestId;
}