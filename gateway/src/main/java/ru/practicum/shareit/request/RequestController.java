package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@RestController
@RequestMapping("/requests")
@RequiredArgsConstructor
@Slf4j
@Validated
public class RequestController {

    private final RequestClient requestClient;

    @PostMapping
    public ResponseEntity<Object> createRequest(
            @RequestHeader("X-Sharer-User-Id") @Min(1) Long requesterId,
            @RequestBody @Valid ItemRequestDto itemRequestDto) {
        log.info("По RequesterId создан requesterId:={} и ItemRequestDto:={}", requesterId, itemRequestDto);
        return requestClient.createRequest(requesterId, itemRequestDto);
    }

    @GetMapping
    public ResponseEntity<Object> getUserRequests(
            @RequestHeader("X-Sharer-User-Id") @Min(1) Long requesterId,
            @PositiveOrZero @RequestParam(value = "from", defaultValue = "0") @Min(0) Integer from,
            @Positive @RequestParam(value = "size", defaultValue = "10") @Min(1) @Max(20) Integer size) {
        log.info("По RequesterId ID requesterId:={} получены запросы.", requesterId);
        return requestClient.getUserRequests(requesterId, from, size);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getDifferentRequests(
            @RequestHeader("X-Sharer-User-Id") @Min(1) Long requesterId,
            @PositiveOrZero @RequestParam(value = "from", defaultValue = "0") @Min(0) Integer from,
            @Positive @RequestParam(value = "size", defaultValue = "10") @Min(1) @Max(20) Integer size) {
        log.info("По RequesterId ID requesterId:={} получены запросы не включающие запросы самого Юзера.", requesterId);
        return requestClient.getDifferentRequests(requesterId, from, size);
    }

    @GetMapping("{requestId}")
    public ResponseEntity<Object> getInfoRequest(
            @RequestHeader("X-Sharer-User-Id") @Min(1) Long userId,
            @PathVariable @Min(1)Long requestId) {
        log.info("Пользователь с ID userId:={} получил информацию по запросам requestId:={}", userId, requestId);
        return requestClient.getInfoRequest(userId, requestId);
    }
}