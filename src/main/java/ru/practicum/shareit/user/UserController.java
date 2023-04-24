package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserDtoResponse;
import ru.practicum.shareit.user.dto.UserDtoUpdate;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequestMapping("/users")
@Validated
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class UserController {
    private final UserService userService;

    @PostMapping
    public ResponseEntity<UserDtoResponse> createUser(@Valid @RequestBody UserDto userDto) {
        log.warn("Создан User: " + userDto + " .");
        return new ResponseEntity<>(userService.createUser(userDto), HttpStatus.CREATED);
    }

    @GetMapping("{id}")
    public ResponseEntity<UserDtoResponse> getUserById(@PathVariable("id") @Min(1) Long userId) {
        log.warn("Получени User userId " + userId + " .");
        return new ResponseEntity<>(userService.getUserById(userId), HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<UserDtoResponse>> getUsers() {
        ResponseEntity<List<UserDtoResponse>> result = new ResponseEntity<>(userService.getUserRepository(), HttpStatus.OK);
        log.warn("Получены Users: " + result.getBody());
        return result;
    }

    @PatchMapping("{id}")
    public ResponseEntity<UserDtoResponse> updateUser(@RequestBody UserDtoUpdate userDtoUpdate,
                                                      @PathVariable("id") Long userId) {
        log.warn(" User: " + userId + " обновлен на следующие данные" + userDtoUpdate + " .");
        return new ResponseEntity<>(userService.updateUser(userDtoUpdate, userId), HttpStatus.OK);
    }

    @DeleteMapping("{id}")
    public void deleteUser(@Min(1) @PathVariable("id") Long userId) {
        userService.deleteUser(userId);
        log.warn("User: " + userId + " удален.");
    }
}