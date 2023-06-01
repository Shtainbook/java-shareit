package ru.practicum.shareit.user.dto;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Data
@Builder
public class UserDtoUpdate {
    @Pattern(regexp = "^[^ ].*[^ ]$", message = "Неккоректное имя")
    @Size(max = 255)
    private String name;
    @Email(message = "Некорректный email")
    private String email;
}