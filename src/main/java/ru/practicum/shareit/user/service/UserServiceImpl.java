package ru.practicum.shareit.user.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.db.UserStorage;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {
    private final UserStorage userStorage;

    @Autowired
    public UserServiceImpl(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    @Override
    public UserDto create(UserDto userDto) {
        User userTest = UserMapper.dtoToUser(userDto);
        User afterStorage = userStorage.create(userTest);
        return UserMapper.toUserDTO(afterStorage);
    }

    @Override
    public UserDto update(UserDto userDto, Long id) {
        return UserMapper.toUserDTO(userStorage.update(UserMapper.dtoToUser(userDto), id));
    }

    @Override
    public UserDto read(Long id) {
        return UserMapper.toUserDTO(userStorage.read(id));
    }

    @Override
    public List<UserDto> readAll() {
        ArrayList<UserDto> element = new ArrayList<>();
        for (User component : userStorage.readAll()
        ) {
            element.add(UserMapper.toUserDTO(component));
        }
        return element;
    }

    @Override
    public UserDto delete(Long id) {
        return UserMapper.toUserDTO(userStorage.delete(id));
    }
}