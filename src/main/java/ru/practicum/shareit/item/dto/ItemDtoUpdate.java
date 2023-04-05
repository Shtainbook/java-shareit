package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Getter;
import ru.practicum.shareit.user.model.User;

import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Builder
@Getter
public class ItemDtoUpdate {
    @Pattern(regexp = "^\\w+.*\\S$", message = "Неккоректное имя")
    @Size(max = 255)
    private String name;
    @Pattern(regexp = "^\\w+.*\\S$", message = "Неккоректное описание")
    @Size(max = 500)
    private String description;
    private User owner;
    private Boolean available;
}
