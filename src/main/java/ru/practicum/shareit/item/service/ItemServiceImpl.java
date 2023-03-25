package ru.practicum.shareit.item.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.db.ItemDb;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemListDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.db.UserDb;

import java.util.ArrayList;
import java.util.List;

@Service
public class ItemServiceImpl implements ItemService {

    private final UserDb userDb;
    private final ItemDb itemDb;

    @Autowired
    public ItemServiceImpl(UserDb userDb, ItemDb itemDb) {
        this.userDb = userDb;
        this.itemDb = itemDb;
    }

    @Override
    public ResponseEntity<ItemDto> createItem(ItemDto item, Long userId) {

        User user = userDb.read(userId);
        return new ResponseEntity<>(ItemMapper.toItemDTO(itemDb.createItem(ItemMapper.DtotoItem(item), user)), HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<ItemDto> readItem(Long id) {
        return new ResponseEntity<>(ItemMapper.toItemDTO(itemDb.readItem(id)), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<List<ItemDto>> readAllItem() {
        return new ResponseEntity<>(ItemMapper.toListItemDto(itemDb.readAllItem()), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<ItemDto> updateItem(Long itemId, Long userId, ItemDto item) {
        User user = userDb.read(userId);
        return new ResponseEntity<>(ItemMapper.toItemDTO
                (itemDb.updateItem(itemId, user, ItemMapper.DtotoItem(item))),
                HttpStatus.OK);
    }

    @Override
    public ResponseEntity<ItemDto> deleteItem(Long id) {
        return null;
    }

    @Override
    public ResponseEntity<List<ItemDto>> searchPersonalItems(Long id) {
        return new ResponseEntity<>(ItemMapper.toListItemDto(itemDb.searchPersonalItems(id)), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<ItemListDto> searchItemsByName(String text) {
        if (text.isBlank()) {
            return new ResponseEntity<>(new ItemListDto(new ArrayList<>()), HttpStatus.OK);
        }
        return new ResponseEntity<>(new ItemListDto((ItemMapper.toListItemDto(itemDb.searchItemsByName(text)))), HttpStatus.OK);
    }
}
