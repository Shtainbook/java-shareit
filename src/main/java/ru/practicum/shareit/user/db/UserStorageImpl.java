package ru.practicum.shareit.user.db;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.shareit.user.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class UserStorageImpl implements UserStorage {
    private final Map<Long, User> allUsers = new HashMap<>();
    private Long userCounter = 1L;

    @Override
    public User create(User user) {
        validateUser(user);
        user.setId(userCounter++);
        allUsers.put(user.getId(), user);
        log.info("добавлен пользователь + " + user.getId());
        return user;
    }

    @Override
    public User read(Long id) {
        if (allUsers.containsKey(id)) {
            log.info("запрошен, найден, передан пользователь + " + id);
        } else {
            log.info("не найден пользователь" + id);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Пользователя с id=" + id + " нет");
        }
        return allUsers.get(id);
    }

    @Override
    public List<User> readAll() {
        log.info("запрошены, выданы все пользователи");
        return new ArrayList<>(allUsers.values());
    }

    @Override
    public User update(User user, Long id) {
        if (allUsers.get(id) == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Пользователя с id=" + id + " нет");
        }
        if (user.getEmail() != null && !user.getEmail().equals(allUsers.get(id).getEmail())) {
            validateUser(user);
        }
        user.setId(id);
        updateUserNameAndEmail(user);
        allUsers.put(id, user);
        log.info("Пользовалель с id={} обновлен", user.getId());
        return allUsers.get(user.getId());
    }

    @Override
    public User delete(Long id) {
        if (allUsers.containsKey(id)) {
            allUsers.remove(id);
            log.info("Пользователь найден и удален + " + id);
            return allUsers.get(id);
        } else {
            log.info("Пользователь не найден и не удален + " + id);
            return null;
        }
    }

    private void validateUser(User user) {
        if (allUsers.values().stream().anyMatch(u -> u.getEmail().equals(user.getEmail()))) {

            log.warn("email {} уже занят", user.getEmail());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "email " + user.getEmail() + " уже занят");
        }
    }

    private void updateUserNameAndEmail(User user) {
        if (user.getName() != null) {
            allUsers.get(user.getId()).setName(user.getName());
        } else {
            user.setName(allUsers.get(user.getId()).getName());
        }
        if (user.getEmail() != null) {
            allUsers.get(user.getId()).setName(user.getEmail());
        } else {
            user.setEmail(allUsers.get(user.getId()).getEmail());
        }
    }
}