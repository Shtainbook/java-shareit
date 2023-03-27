package ru.practicum.shareit.user;

import lombok.NoArgsConstructor;
import ru.practicum.shareit.user.dto.UserDto;

@NoArgsConstructor
public class UserMapper {
    public static UserDto toUserDTO(User user) {
        if (user == null) {
            return null;
        }
        return new UserDto(
                user.getId(),
                user.getName(),
                user.getEmail()
        );
    }

    public static User dtoToUser(UserDto userDto) {
        if (userDto == null) {
            return null;
        }
        return new User(
                userDto.getId(),
                userDto.getName(),
                userDto.getEmail()
        );
    }
}