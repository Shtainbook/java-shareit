package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemListDto;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequestMapping("/items")
public class ItemController {
    private final ItemService itemService;

    @Autowired
    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @PostMapping
    public ResponseEntity<ItemDto> createItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                                              @Valid
                                              @RequestBody ItemDto itemDto) {
        return new ResponseEntity<>(itemService.createItem(itemDto, userId), HttpStatus.CREATED);
    }

    @GetMapping("{id}")
    public ResponseEntity<ItemDto> readItem(@PathVariable Long id) {
        return new ResponseEntity<>(itemService.readItem(id), HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<ItemDto>> searchPersonalItems(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return new ResponseEntity<>(itemService.searchPersonalItems(userId), HttpStatus.OK);
    }

    @GetMapping("search")
    public ResponseEntity<ItemListDto> searchItemsByName(@RequestParam String text) {
        return new ResponseEntity<>(itemService.searchItemsByName(text), HttpStatus.OK);
    }

    @GetMapping("/all")
    public ResponseEntity<List<ItemDto>> readAllItem() {
        return new ResponseEntity<>(itemService.readAllItem(), HttpStatus.OK);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<ItemDto> deleteItem(@PathVariable Long id) {

        return new ResponseEntity<>(itemService.deleteItem(id),HttpStatus.OK);
    }

    @PatchMapping("{itemId}")
    public ResponseEntity<ItemDto> updateItem(@RequestHeader("X-Sharer-User-Id") @Min(1) Long userId,
                                              @RequestBody ItemDto itemDto,
                                              @PathVariable Long itemId) {
        return new ResponseEntity<>(itemService.updateItem(itemId, userId, itemDto),
                HttpStatus.OK);
    }
}

