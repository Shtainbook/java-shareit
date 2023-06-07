package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoUpdate;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemController {

    private final ItemClient itemClient;

    @PostMapping
    public ResponseEntity<Object> createItem(@RequestHeader("X-Sharer-User-Id") @Min(1) Long userId,
                                             @RequestBody @Valid ItemDto itemDto) {

        log.info("Item создан для userId:={} и itemDto:={} ", userId, itemDto);
        return itemClient.createItem(userId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> updateItem(@RequestHeader("X-Sharer-User-Id") @Min(1) Long userId,
                                             @RequestBody ItemDtoUpdate itemDtoUpdate, //убрали валид
                                             @PathVariable @Min(1) Long itemId) {
        log.info("Item с userID:={} и ItemDtoUpdate:={} обновлен для itemId:={}", userId, itemDtoUpdate, itemId);
        return itemClient.updateItem(userId, itemDtoUpdate, itemId);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItemByItemId(@RequestHeader("X-Sharer-User-Id") @Min(1) Long userId,
                                                  @PathVariable @Min(1) Long itemId) {
        log.info("Получен в методе getItemByItemId Item по userId:={} и itemId:={}", userId, itemId);
        return itemClient.getItemByItemId(userId, itemId);
    }

    @GetMapping
    public ResponseEntity<Object> getPersonalItems(
            @RequestHeader("X-Sharer-User-Id") @Min(1) Long userId,
            @RequestParam(value = "from", defaultValue = "0") @Min(0) Integer from,
            @RequestParam(value = "size", defaultValue = "10") @Min(1) @Max(20) Integer size) {
        log.info("В результате вызова метода getPersonalItems для userId:={}", userId);
        return itemClient.getPersonalItems(userId, from, size);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> addComment(@PathVariable @Min(1) Long itemId,
                                             @RequestHeader("X-Sharer-User-Id") @Min(1) Long userId,
                                             @RequestBody @Valid CommentDto commentDto) {
        log.info("В результате вызова метода addComment для itemId:={} пользователем userId:={} был сделан комментарий", itemId, userId);
        return itemClient.addComment(itemId, userId, commentDto);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> getFoundItems(
            @RequestParam String text,
            @RequestParam(value = "from", defaultValue = "0") @Min(0) Integer from,
            @RequestParam(value = "size", defaultValue = "10") @Min(1) @Max(20) Integer size) {
        log.info("В результате вызова метода getFoundItems по значению переменной text:={} ", text);
        return itemClient.getFoundItems(text, from, size);
    }
}