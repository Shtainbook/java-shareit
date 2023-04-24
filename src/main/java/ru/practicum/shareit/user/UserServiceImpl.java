package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserDtoResponse;
import ru.practicum.shareit.user.dto.UserDtoUpdate;
import ru.practicum.shareit.user.dto.UserListDto;
import ru.practicum.shareit.user.model.User;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class UserServiceImpl implements UserService {
    private final UserRepository users;

    private final UserMapper mapper;

    @Override
    public UserDtoResponse createUser(UserDto user) {
        return mapper.mapToUserDtoResponse(users.save(mapper.mapToUserFromUserDto(user)));
    }

    @Override
    public UserDtoResponse getUserById(Long id) {
        return mapper.mapToUserDtoResponse(users.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Пользователя с id=" + id + " нет"))
        );
    }

    @Override
    public UserListDto getUsers() {
        return UserListDto.builder()
                .users(users.findAll().stream().map(mapper::mapToUserDtoResponse).collect(Collectors.toList()))
                .build();
    }

    @Override
    @Transactional
    public UserDtoResponse updateUser(UserDtoUpdate user, Long userId) {
        User updatingUser = users.findById(userId).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Пользователя с id=" + userId + " нет"));
        return mapper.mapToUserDtoResponse(users.save(mapper.mapToUserFromUserDtoUpdate(user, updatingUser)));
    }

    @Override
    public void deleteUser(Long id) {
        users.deleteById(id);
    }
}
