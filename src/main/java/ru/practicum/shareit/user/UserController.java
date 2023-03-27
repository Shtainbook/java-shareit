package ru.practicum.shareit.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.user.service.UserServiceImpl;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.util.List;

/**
 * TODO Sprint add-controllers.
 */

@RestController
@RequestMapping(path = "/users")
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserServiceImpl userService) {
        this.userService = userService;
    }

    @GetMapping("{id}")
    public ResponseEntity<UserDto> getUser(@PathVariable @Min(1) long id) {
        return new ResponseEntity<>(userService.read(id), HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<UserDto>> getAllUser() {
        return new ResponseEntity<>(userService.readAll(), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<UserDto> createUser(@Valid @RequestBody UserDto userDto) {
        return new ResponseEntity<>(userService.create(userDto), HttpStatus.OK);
    }

    @PatchMapping("{id}")
    public ResponseEntity<UserDto> updateUser(@RequestBody UserDto userDto, @PathVariable long id) {
        UserDto userDtoTwo = userService.update(userDto, id);
        System.out.println(userDtoTwo);
        return new ResponseEntity<>(userService.update(userDto, id), HttpStatus.OK);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<UserDto> deleteUser(@Min(1) @PathVariable long id) {
        return new ResponseEntity<>(userService.delete(id), HttpStatus.OK);
    }
}