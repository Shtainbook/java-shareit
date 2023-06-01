package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserDtoUpdate;

import javax.validation.Valid;
import javax.validation.constraints.Min;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Slf4j
@Validated
public class UserController {


    private final UserClient userClient;

    @PostMapping
    public ResponseEntity<Object> createUser(@RequestBody @Valid UserDto userDto) {
        log.info("Создан User userDto:={}.", userDto);
        return userClient.createUser(userDto);
    }

    @GetMapping("{id}")
    public ResponseEntity<Object> getUserById(@PathVariable("id") @Min(1) Long userId) {
        log.info("Получени User userId:={}.", userId);
        return userClient.getUserById(userId);
    }

    @GetMapping
    public ResponseEntity<Object> getUsers() {
        log.info("Получены Users");
        return userClient.getUsers();
    }

    @PatchMapping("{id}")
    public ResponseEntity<Object> updateUser(@RequestBody UserDtoUpdate userDtoUpdate,
                                             @PathVariable("id") Long userId) {
        log.info("User userId:={} обновлен на следующие данные userDtoUpdate:={}.", userId, userDtoUpdate);
        return userClient.updateUser(userDtoUpdate, userId);
    }

    @DeleteMapping("{id}")
    public void deleteUser(@PathVariable("id") @Min(1) Long userId) {
        userClient.deleteUser(userId);
        log.info("User userId:={} удален.", userId);
    }
}