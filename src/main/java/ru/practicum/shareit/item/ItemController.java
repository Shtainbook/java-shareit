package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
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
        return itemService.createItem(itemDto, userId);
    }

    @GetMapping("{id}")
    public ResponseEntity<ItemDto> readItem(@PathVariable Long id) {
        return itemService.readItem(id);
    }

    @GetMapping
    public ResponseEntity<List<ItemDto>> searchPersonalItems(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemService.searchPersonalItems(userId);
    }

    @GetMapping("search")
    public ResponseEntity<ItemListDto> searchItemsByName(@RequestParam String text) {
        // ?
        return itemService.searchItemsByName(text);
    }

    @GetMapping("/all")
    public ResponseEntity<List<ItemDto>> readAllItem() {
        return itemService.readAllItem();
    }

    @DeleteMapping("{id}")
    public ResponseEntity<ItemDto> deleteItem(@PathVariable Long id) {
        return itemService.deleteItem(id);
    }

    @PatchMapping("{itemId}")
    public ResponseEntity<ItemDto> updateItem(@RequestHeader("X-Sharer-User-Id") @Min(1) Long userId,
                                              @RequestBody ItemDto itemDto,
                                              @PathVariable Long itemId) {
        return itemService.updateItem(itemId, userId, itemDto);
    }
}

