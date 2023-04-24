package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.*;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@Slf4j
@RestController
@RequestMapping("/items")
@Validated
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ItemController {
    private final ItemService itemService;

    @PostMapping
    public ResponseEntity<ItemDtoResponse> createItem(@RequestHeader("X-Sharer-User-Id") @Min(1) Long userId,
                                                      @Valid @RequestBody ItemDto itemDto) {
        log.warn("Item создан ID: " + userId + " и ItemDto: " + itemDto + ".");
        return new ResponseEntity<>(itemService.createItem(itemDto, userId), HttpStatus.CREATED);
    }

    @PatchMapping("{itemId}")
    public ResponseEntity<ItemDtoResponse> updateItem(@RequestHeader("X-Sharer-User-Id") @Min(1) Long userId,
                                                      @RequestBody ItemDtoUpdate itemDtoUpdate,
                                                      @PathVariable @Min(1) Long itemId) {
        log.warn("Item с userID: " + userId + " и ItemDtoUpdate: " + itemDtoUpdate + " обновлен для itemId " + itemId + ".");
        return new ResponseEntity<>(itemService.updateItem(itemId, userId, itemDtoUpdate), HttpStatus.OK);
    }

    @GetMapping("{itemId}")
    public ResponseEntity<ItemDtoResponse> getItemByItemId(@RequestHeader("X-Sharer-User-Id") @Min(1) Long userId,
                                                           @PathVariable @Min(1) Long itemId) {
        log.warn("Получен Item по userId: " + userId + " и itemId " + itemId + ".");
        return new ResponseEntity<>(itemService.getItemByItemId(userId, itemId), HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<ItemDtoResponse>> getPersonalItems(
            @RequestHeader("X-Sharer-User-Id") @Min(1) Long userId,
            @RequestParam(value = "from", defaultValue = "0") @Min(0) Integer from,
            @RequestParam(value = "size", defaultValue = "10") @Min(1) @Max(20) Integer size) {
        ResponseEntity<List<ItemDtoResponse>> result = new ResponseEntity<>(itemService.
                getPersonalItems(PageRequest.of(from / size, size), userId), HttpStatus.OK);
        log.warn("В результате вызова метода getPersonalItems для userId: " + userId + " результат: " + result.getBody() + " .");
        return result;
    }

    @GetMapping("search")
    public ResponseEntity<List<ItemDtoResponse>> getFoundItems(
            @RequestParam String text,
            @RequestParam(value = "from", defaultValue = "0") @Min(0) Integer from,
            @RequestParam(value = "size", defaultValue = "10") @Min(1) @Max(20) Integer size) {
        ResponseEntity<List<ItemDtoResponse>> result = new ResponseEntity<>(itemService.
                getFoundItems(PageRequest.of(from / size, size), text), HttpStatus.OK);
        log.warn("В результате вызова метода getFoundItems по значению переменной text: " + text + " был найден результат: " + result.getBody() + " .");
        return result;
    }

    @PostMapping("{itemId}/comment")
    public ResponseEntity<CommentDtoResponse> addComment(@PathVariable @Min(1) Long itemId,
                                                         @RequestHeader("X-Sharer-User-Id") @Min(1) Long userId,
                                                         @Valid @RequestBody CommentDto commentDto) {
        ResponseEntity<CommentDtoResponse> result = new ResponseEntity<>(itemService.addComment(itemId, userId, commentDto), HttpStatus.OK);
        log.warn("В результате вызова метода addComment для ItemId: " + itemId + " пользователем " + userId + " был сделан комментарий: " + result.getBody() + " .");
        return result;
    }
}