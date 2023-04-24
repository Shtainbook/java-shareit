package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final ItemMapper itemMapper;
    private final ItemRequestRepository itemRequestRepository;

    @Override
    @Transactional
    public ItemDtoResponse createItem(ItemDto item, Long userId) throws ResponseStatusException {
        Item newItem = itemMapper.mapToItemFromItemDto(item);
        if (item.getRequestId() != null) {
            ItemRequest itemRequest = itemRequestRepository.findById(item.getRequestId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Запроса с id=" +
                            item.getRequestId() + " нет"));
            newItem.setRequest(itemRequest);
        }
        newItem.setOwner(userRepository.findById(userId).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Пользователя с id=" + userId + " нет")));
        return itemMapper.mapToItemDtoResponse(itemRepository.save(newItem));
    }

    @Override
    @Transactional
    public ItemDtoResponse updateItem(Long itemId, Long userId, ItemDtoUpdate item) {
        Item updateItem = itemRepository.findById(itemId).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Предмета с id=" + itemId + " нет"));
        if (updateItem.getOwner().getId().equals(userId)) {
            return itemMapper.mapToItemDtoResponse(itemRepository.save(itemMapper.mapToItemFromItemDtoUpdate(item, updateItem)));
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                "Предмет с id=" + itemId + " пользователю с id=" + userId + " не пренадлежит");
    }

    @Override
    @Transactional(readOnly = true)
    public ItemDtoResponse getItemByItemId(Long userId, Long itemId) {
        Item item = itemRepository.findById(itemId).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Предмета с id=" + itemId + " нет"));
        ItemDtoResponse itemDtoResponse = itemMapper.mapToItemDtoResponse(item);
        if (item.getOwner().getId().equals(userId)) {
            Booking lastBooking = bookingRepository.findFirstByItemIdAndEndBeforeAndStatusOrderByEndDesc(itemId, LocalDateTime.now(), Status.APPROVED);
            if (lastBooking == null) {
                lastBooking = bookingRepository.findFirstByItemIdAndStartBeforeAndEndAfterAndStatusOrderByEndDesc(itemId, LocalDateTime.now(), LocalDateTime.now(), Status.APPROVED);
            }
            itemDtoResponse.setLastBooking(itemMapper
                    .mapToBookingShortDto(lastBooking));
            itemDtoResponse.setNextBooking(itemMapper.mapToBookingShortDto(bookingRepository
                    .findFirstByItemIdAndStartAfterAndStatusOrderByStartAsc(
                            itemId, LocalDateTime.now(), Status.APPROVED)
            ));
            return itemDtoResponse;
        }
        return itemDtoResponse;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemDtoResponse> getPersonalItems(Pageable pageable, Long userId) {
        if (userRepository.existsById(userId)) {
            List<ItemDtoResponse> personalItems = itemRepository.findAllByOwnerId(pageable, userId).stream()
                    .map(itemMapper::mapToItemDtoResponse).collect(Collectors.toList());
            for (ItemDtoResponse item : personalItems) {
                item.setLastBooking(itemMapper.mapToBookingShortDto(bookingRepository.findFirstByItemIdAndEndBeforeAndStatusOrderByEndDesc(
                        item.getId(), LocalDateTime.now(), Status.APPROVED)));
                item.setNextBooking(itemMapper.mapToBookingShortDto(bookingRepository
                        .findFirstByItemIdAndStartAfterAndStatusOrderByStartAsc(
                                item.getId(), LocalDateTime.now(), Status.APPROVED)));
            }
            return personalItems;
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Пользователя с id=" + userId + " не существует");
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemDtoResponse> getFoundItems(Pageable pageable, String text) {
        if (text.isBlank()) {
            return new ArrayList<>();
        }
        return itemRepository.findAllByNameOrDescriptionContainingIgnoreCaseAndAvailableTrue(pageable, text, text).stream()
                .map(itemMapper::mapToItemDtoResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CommentDtoResponse addComment(Long itemId, Long userId, CommentDto commentDto) {
        if (bookingRepository.existsBookingByItemIdAndBookerIdAndStatusAndEndIsBefore(itemId, userId,
                Status.APPROVED, LocalDateTime.now())) {
            User author = userRepository.findById(userId).orElseThrow(
                    () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Пользователя с id=" + userId + " нет"));
            Item item = itemRepository.findById(itemId).orElseThrow(
                    () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Предмета с id=" + itemId + " нет"));
            Comment comment = itemMapper.mapToCommentFromCommentDto(commentDto);
            comment.setItem(item);
            comment.setAuthor(author);
            comment.setCreated(LocalDateTime.now());
            return itemMapper.mapToCommentDtoResponseFromComment(commentRepository.save(comment));
        }
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "У пользователя с id=" + userId + " не было ни одной брони на предмет с id=" + itemId);
    }
}