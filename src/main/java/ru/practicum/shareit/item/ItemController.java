package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.*;

import javax.validation.Valid;
import javax.validation.constraints.Min;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ItemController {
    private final ItemService itemService;

    @PostMapping
    public ResponseEntity<ItemDtoResponse> createItem(@RequestHeader("X-Sharer-User-Id") @Min(1) Long userId,
                                                      @Valid @RequestBody ItemDto itemDto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(itemService.createItem(itemDto, userId));
    }

    @PatchMapping("{itemId}")
    public ResponseEntity<ItemDtoResponse> updateItem(@RequestHeader("X-Sharer-User-Id") @Min(1) Long userId,
                                                      @RequestBody ItemDtoUpdate itemDtoUpdate,
                                                      @PathVariable Long itemId) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(itemService.updateItem(itemId, userId, itemDtoUpdate));
    }

    @GetMapping("{itemId}")
    public ResponseEntity<ItemDtoResponse> getItemByItemId(@RequestHeader("X-Sharer-User-Id") @Min(1) Long userId,
                                                           @PathVariable Long itemId) {
        return ResponseEntity.status(HttpStatus.OK).body(itemService.getItemByItemId(userId, itemId));
    }

    @GetMapping
    public ResponseEntity<ItemListDto> getPersonalItems(@RequestHeader("X-Sharer-User-Id") @Min(1) Long userId) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(itemService.getPersonalItems(userId));
    }

    @GetMapping("search")
    public ResponseEntity<ItemListDto> getFoundItems(@RequestParam String text) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(itemService.getFoundItems(text));
    }

    @PostMapping("{itemId}/comment")
    public ResponseEntity<CommentDtoResponse> addComment(@PathVariable @Min(1) Long itemId,
                                                         @RequestHeader("X-Sharer-User-Id") @Min(1) Long userId,
                                                         @Valid @RequestBody CommentDto commentDto) {
        return ResponseEntity.status(HttpStatus.OK).body(itemService.addComment(itemId, userId, commentDto));
    }

}
