package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserService {
    UserDto create(UserDto userDto);

    UserDto read(Long id);

    List<UserDto> readAll();

    UserDto update(UserDto userDto, Long id);

    UserDto delete(Long id);
}
