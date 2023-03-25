package ru.practicum.shareit.user.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.db.UserDb;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {
    private final UserDb userDb;

    @Autowired
    public UserServiceImpl(UserDb userDb) {
        this.userDb = userDb;
    }

    @Override
    public ResponseEntity<UserDto> create(UserDto userDto) {
        User userTest = UserMapper.dtoToUser(userDto);
        User afterStorage = userDb.create(userTest);
        UserDto erken = UserMapper.toUserDTO(afterStorage);
        return new ResponseEntity<>(erken, HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<UserDto> update(UserDto userDto, Long id) {
        return new ResponseEntity<>(UserMapper.toUserDTO(userDb.update(UserMapper.dtoToUser(userDto), id)), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<UserDto> read(Long id) {
        return new ResponseEntity<>(UserMapper.toUserDTO(userDb.read(id)), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<List<UserDto>> readAll() {
        ArrayList<UserDto> element = new ArrayList<>();
        for (User component : userDb.readAll()
        ) {
            element.add(UserMapper.toUserDTO(component));
        }
        return new ResponseEntity<>(element, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<UserDto> delete(Long id) {
        return new ResponseEntity<>(UserMapper.toUserDTO(userDb.delete(id)), HttpStatus.OK);
    }
}