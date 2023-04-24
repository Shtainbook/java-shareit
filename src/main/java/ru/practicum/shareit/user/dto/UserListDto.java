package ru.practicum.shareit.user.dto;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Builder;

import java.util.List;

@Builder
public class UserListDto {
    @JsonValue
    private List<UserDtoResponse> users;
}
