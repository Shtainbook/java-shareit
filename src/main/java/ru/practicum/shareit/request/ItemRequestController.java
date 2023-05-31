package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoResponse;
import ru.practicum.shareit.request.dto.RequestDtoResponseWithMD;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.util.List;

/**
 * TODO Sprint add-item-requests.
 */
@Slf4j
@RestController
@RequestMapping("/requests")
@Validated
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ItemRequestController {
    private final ItemRequestService itemRequestService;

    @PostMapping
    public ResponseEntity<ItemRequestDtoResponse> createRequest(@RequestHeader("X-Sharer-User-Id") @Min(1) Long requesterId,
                                                                @RequestBody @Valid ItemRequestDto itemRequestDto) {
        log.info("По RequesterId создан ID: " + requesterId + " и ItemRequestDto: " + itemRequestDto + ".");
        return new ResponseEntity<>(itemRequestService.createItemRequest(itemRequestDto, requesterId), HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<RequestDtoResponseWithMD>> getUserRequests(
            @RequestHeader("X-Sharer-User-Id") @Min(1) Long requesterId,
            @RequestParam(value = "from", defaultValue = "0") @Min(0) Integer from,
            @RequestParam(value = "size", defaultValue = "10") @Min(1) @Max(20) Integer size) {
        ResponseEntity<List<RequestDtoResponseWithMD>> result = new ResponseEntity<>(itemRequestService.getUserRequests(
                PageRequest.of(from / size, size).withSort(Sort.by("created").descending()),
                requesterId), HttpStatus.OK);
        log.info("По RequesterId ID: " + requesterId + " получены запросы.");
        return result;
    }

    @GetMapping("all")
    public ResponseEntity<List<RequestDtoResponseWithMD>> getDifferentRequests(
            @RequestHeader("X-Sharer-User-Id") @Min(1) Long requesterId,
            @RequestParam(value = "from", defaultValue = "0") @Min(0) Integer from,
            @RequestParam(value = "size", defaultValue = "10") @Min(1) @Max(20) Integer size) {
        ResponseEntity<List<RequestDtoResponseWithMD>> result = new ResponseEntity<>(itemRequestService.getDifferentRequests(
                PageRequest.of(from / size, size, Sort.by(Sort.Direction.DESC, "created")),
                requesterId), HttpStatus.OK);
        log.info("По RequesterId ID: " + requesterId + " получены запросы не включающие запросы самого Юзера.");
        return result;
    }

    @GetMapping("{requestId}")
    public ResponseEntity<RequestDtoResponseWithMD> getInfoRequest(
            @RequestHeader("X-Sharer-User-Id") @Min(1) Long userId,
            @PathVariable @Min(1) Long requestId) {
        ResponseEntity<RequestDtoResponseWithMD> result = new ResponseEntity<>(itemRequestService.getInfoRequest(userId, requestId), HttpStatus.OK);
        log.info("Пользователь с ID: " + userId + " получил информацию по запросам " + requestId + " .");
        return result;
    }
}