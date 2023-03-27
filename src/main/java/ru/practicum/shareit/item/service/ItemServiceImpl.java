package ru.practicum.shareit.item.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.db.ItemStorage;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemListDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.db.UserStorage;

import java.util.ArrayList;
import java.util.List;

@Service
public class ItemServiceImpl implements ItemService {

    private final UserStorage userStorage;
    private final ItemStorage itemStorage;

    @Autowired
    public ItemServiceImpl(UserStorage userDb, ItemStorage itemDb) {
        this.userStorage = userDb;
        this.itemStorage = itemDb;
    }

    @Override
    public ItemDto createItem(ItemDto item, Long userId) {

        User user = userStorage.read(userId);
        return ItemMapper.toItemDTO(itemStorage.createItem(ItemMapper.dtoToItem(item), user));
    }

    @Override
    public ItemDto readItem(Long id) {
        return ItemMapper.toItemDTO(itemStorage.readItem(id));
    }

    @Override
    public List<ItemDto> readAllItem() {
        return ItemMapper.toListItemDto(itemStorage.readAllItem());
    }

    @Override
    public ItemDto updateItem(Long itemId, Long userId, ItemDto item) {
        User user = userStorage.read(userId);
        return ItemMapper.toItemDTO(
                itemStorage.updateItem(itemId, user, ItemMapper.dtoToItem(item)));
    }

    @Override
    public ItemDto deleteItem(Long id) {
        return null;
    }

    @Override
    public List<ItemDto> searchPersonalItems(Long id) {
        return ItemMapper.toListItemDto(itemStorage.searchPersonalItems(id));
    }

    @Override
    public ItemListDto searchItemsByName(String text) {
        if (text.isBlank()) {
            return new ItemListDto(new ArrayList<>());
        }
        return new ItemListDto(ItemMapper.toListItemDto(itemStorage.searchItemsByName(text)));
    }
}
