package ru.practicum.shareit.user;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserDtoResponse;
import ru.practicum.shareit.user.dto.UserDtoUpdate;
import ru.practicum.shareit.user.dto.UserListDto;

public interface UserService {
    UserDtoResponse createUser(UserDto user);

    UserDtoResponse getUserById(Long id);

    UserListDto getUsers();

    UserDtoResponse updateUser(UserDtoUpdate userDto, Long userId);

    void deleteUser(Long id);
}
