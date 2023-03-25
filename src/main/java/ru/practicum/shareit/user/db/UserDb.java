package ru.practicum.shareit.user.db;

import ru.practicum.shareit.user.User;

import java.util.List;

public interface UserDb {
    User create(User user);

    User read(Long id);

    List<User> readAll();

    User update(User user, Long id);

    User delete(Long id);
}
