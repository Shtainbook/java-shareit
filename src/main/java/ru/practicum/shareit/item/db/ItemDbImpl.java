package ru.practicum.shareit.item.db;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.shareit.error.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.util.*;
import java.util.stream.Collectors;

@Component
@Slf4j
public class ItemDbImpl implements ItemDb {

    private final Map<Long, List<Item>> allItems = new HashMap<>();
    private Long itemCounter = 1L;

    @Override
    public Item createItem(Item item, User user) {
        item.setId(itemCounter++);
        item.setOwner(user);
        if (allItems.containsKey(user.getId())) {
            allItems.get(user.getId()).add(item);
        } else {
            allItems.put(user.getId(), new ArrayList<>(List.of(item)));
        }
        log.info("Вещь {} добавлена пользователем {}", item, user);
        return item;
    }

    @Override
    public Item readItem(Long id) {
        Optional<Item> element = allItems.values().stream().flatMap(Collection::stream).
                filter(a -> Objects.equals(a.getId(), id)).findFirst();//приходит id предмета или owner?
        if (element.isPresent()) {
            log.info("Вещь {} выдана пользователю {}", element.get().getName(), id);
            return element.get();
        }
        log.info(id + " - этот пользователь не имеет Items");
        throw new NotFoundException("Предмет не найден");
    }

    @Override
    public List<Item> readAllItem() {
        log.info("Все Items получены.");
        return allItems.values().stream().flatMap(Collection::stream).collect(Collectors.toList());
    }

    @Override
    public Item updateItem(Long itemId, User user, Item item) {
        checkOwner(itemId, user);
        Item element = allItems.get(user.getId()).stream().
                filter(a -> a.getId().equals(itemId)).findFirst().
                orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Нет такого Item тут!"));
        item.setOwner(user);
        item.setId(itemId);
        if (item.getName() == null) {
            item.setName(element.getName());
        }
        if (item.getAvailable() == null) {
            item.setAvailable(element.getAvailable());
        }
        if (item.getDescription() == null) {
            item.setDescription(element.getDescription());
        }
        allItems.get(user.getId()).remove(element);
        allItems.get(user.getId()).add(item);
        log.info("Пользователь {} обновил вещь {}", user, item);
        return item;
    }

    @Override
    public Item deleteItem(Long id) {
        return null;
    }

    @Override
    public List<Item> searchPersonalItems(Long id) {
        List<Item> element = allItems.values().stream().flatMap(Collection::stream).
                filter(a -> Objects.equals(a.getOwner().getId(), id)).collect(Collectors.toList());
        log.info("Вещи пользователя " + id + " найдены");
        return element;
    }

    @Override
    public List<Item> searchItemsByName(String text) {
        String search = text.toLowerCase();
        List<Item> element = new ArrayList<>();
        allItems.values().forEach(
                itemsList -> element.addAll
                        (
                                itemsList.stream().filter(item -> (item.getDescription().toLowerCase().contains(search)
                                                || item.getName().toLowerCase().contains(search))
                                                && item.getAvailable())
                                        .collect(Collectors.toList())));
        return element;
    }

    private void checkOwner(Long itemId, User user) {
        if (allItems.get(user.getId()) == null || allItems.get(user.getId()).stream().noneMatch(i -> i.getId().equals(itemId))) {
            log.info("Вещь с id={} не пренадлежит пользователю с id={}", itemId, user.getId());
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Вещь с id=" + itemId + " не пренадлежит пользователю с id=" + user.getId());
        }
    }
}