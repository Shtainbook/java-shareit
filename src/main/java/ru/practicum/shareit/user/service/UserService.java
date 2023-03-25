package ru.practicum.shareit.user.service;

import org.springframework.http.ResponseEntity;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserService {
    ResponseEntity<UserDto> create(UserDto userDto);

    ResponseEntity<UserDto> read(Long id);

    ResponseEntity<List<UserDto>> readAll();

    ResponseEntity<UserDto> update(UserDto userDto, Long id);

    ResponseEntity<UserDto> delete(Long id);
}
