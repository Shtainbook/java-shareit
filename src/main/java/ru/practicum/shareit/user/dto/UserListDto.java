package ru.practicum.shareit.user.dto;

import com.fasterxml.jackson.annotation.JsonValue;

import java.util.List;

public class UserListDto {
    @JsonValue
    private List<UserDto> usersDto;
}
