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
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public UserDtoResponse createUser(UserDto user) {
        return userMapper.mapToUserDtoResponse(userRepository.save(userMapper.mapToUserFromUserDto(user)));
    }

    @Override
    public UserDtoResponse getUserById(Long id) {
        return userMapper.mapToUserDtoResponse(userRepository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Пользователя с id=" + id + " нет"))
        );
    }

    public List<UserDtoResponse> getUserRepository() {
        return userRepository.findAll().stream().map(userMapper::mapToUserDtoResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public UserDtoResponse updateUser(UserDtoUpdate user, Long userId) {
        User updatingUser = userRepository.findById(userId).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Пользователя с id=" + userId + " нет"));
        return userMapper.mapToUserDtoResponse(userRepository.save(userMapper.mapToUserFromUserDtoUpdate(user, updatingUser)));
    }

    @Override
    public void deleteUser(Long id) {
        if (userRepository.existsById(id)) {
            userRepository.deleteById(id);
            return;
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Пользователя с id=" + id + " нет");
    }
}
