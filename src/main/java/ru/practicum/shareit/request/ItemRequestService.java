package ru.practicum.shareit.request;

import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoResponse;
import ru.practicum.shareit.request.dto.RequestDtoResponseWithMD;

import java.util.List;

public interface ItemRequestService {
    ItemRequestDtoResponse createItemRequest(ItemRequestDto itemRequestDto, Long requesterId);

    List<RequestDtoResponseWithMD> getUserRequests(PageRequest pageRequest, Long requesterId);

    List<RequestDtoResponseWithMD> getDifferentRequests(PageRequest pageRequest, Long requesterId);

    RequestDtoResponseWithMD getInfoRequest(Long userId, Long requestId);
}
