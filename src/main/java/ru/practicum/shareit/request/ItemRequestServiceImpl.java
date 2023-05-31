package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoResponse;
import ru.practicum.shareit.request.dto.RequestDtoResponseWithMD;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestRepository itemRequestRepository;
    private final UserRepository userRepository;
    private final ItemRequestMapper itemRequestMapper;

    @Override
    public ItemRequestDtoResponse createItemRequest(ItemRequestDto itemRequestDto, Long requesterId) {
        User user = userRepository.findById(requesterId).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Пользователя с id=" + requesterId + " нет"));
        ItemRequest newRequest = itemRequestMapper.mapToItemRequest(itemRequestDto);
        newRequest.setRequester(user);
        newRequest.setCreated(LocalDateTime.now());
        return itemRequestMapper.mapToItemRequestDtoResponse(itemRequestRepository.save(newRequest));
    }

    @Override
    public List<RequestDtoResponseWithMD> getUserRequests(PageRequest pageRequest, Long requesterId) {
        if (userRepository.existsById(requesterId)) {
            return itemRequestMapper.mapToRequestDtoResponseWithMD(itemRequestRepository.findAllByRequesterId(pageRequest, requesterId));
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Пользователя с id=" + requesterId + " нет");
    }

    @Override
    public List<RequestDtoResponseWithMD> getDifferentRequests(PageRequest pageRequest, Long requesterId) {
        if (userRepository.existsById(requesterId)) {
            return itemRequestMapper.mapToRequestDtoResponseWithMD(itemRequestRepository.findAllByRequesterIdNot(pageRequest, requesterId));
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Пользователя с id=" + requesterId + " нет");
    }

    @Override
    public RequestDtoResponseWithMD getInfoRequest(Long userId, Long requestId) {
        if (userRepository.existsById(userId)) {
            return itemRequestMapper.mapToRequestDtoResponseWithMD(
                    itemRequestRepository.findById(requestId)
                            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                                    "Запроса с id=" + requestId + " нет")));
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Пользователя с id=" + userId + " нет");
    }
}