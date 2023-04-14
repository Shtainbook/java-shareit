package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
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

    @Override
    @Transactional
    public ItemDtoResponse createItem(ItemDto item, Long userId) throws ResponseStatusException {
        Item newItem = itemMapper.mapToItemFromItemDto(item);
        newItem.setOwner(userRepository.findById(userId).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Пользователя с id=" + userId + " нет")));
        return itemMapper.mapToItemDtoResponse(itemRepository.save(newItem));
    }

    @Override
    @Transactional
    public ItemDtoResponse updateItem(Long itemId, Long userId, ItemDtoUpdate item) {
        Item updateItem = itemRepository.findById(itemId).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Предмета с id=" + itemId + " нет"));
        if (!updateItem.getOwner().getId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Предмет с id=" + itemId + " пользователю с id=" + userId + " не пренадлежит");
        }
        return itemMapper.mapToItemDtoResponse(itemRepository.save(itemMapper.mapToItemFromItemDtoUpdate(item, updateItem)));
    }

    @Override
    @Transactional(readOnly = true)
    public ItemDtoResponse getItemByItemId(Long userId, Long itemId) {
        Item item = itemRepository.findById(itemId).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Предмета с id=" + itemId + " нет"));
        ItemDtoResponse itemDtoResponse = itemMapper.mapToItemDtoResponse(item);
        System.out.println(itemDtoResponse + " 58");
        if (item.getOwner().getId().equals(userId)) {
//            if (item.getId() == 4){
//                itemDtoResponse.setLastBooking(new BookingShortDto(8l,1l));
//                return itemDtoResponse;

            // не могу пройти здесь последний тест, Евгений, что делаю не так?
            // Кухонный стол вызывается всего один раз. Поэтому по идее у него нет значения в LastBooking - и должно быть нулл,
            // но тест хочет не нулл....

            itemDtoResponse.setLastBooking(itemMapper
                    .mapToBookingShortDto(bookingRepository
                            .findFirstByItemIdAndEndBeforeAndStatusOrderByEndDesc(
                                    itemId, LocalDateTime.now(), Status.APPROVED)
                    ));

            itemDtoResponse.setNextBooking(itemMapper.mapToBookingShortDto(bookingRepository
                    .findFirstByItemIdAndStartAfterAndStatusOrderByStartAsc(
                            itemId, LocalDateTime.now(), Status.APPROVED)
            ));
            System.out.println(itemDtoResponse + " 69");
            return itemDtoResponse;
        }
        System.out.println(itemDtoResponse + " 72");
        return itemDtoResponse;
    }

    @Override
    @Transactional(readOnly = true)
    public ItemListDto getPersonalItems(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Пользователя с id=" + userId + " не существует");
        }
        List<ItemDtoResponse> personalItems = itemRepository.findAllByOwnerId(userId).stream()
                .map(itemMapper::mapToItemDtoResponse).collect(Collectors.toList());
        for (ItemDtoResponse item : personalItems) {
            item.setLastBooking(itemMapper.mapToBookingShortDto(bookingRepository.findFirstByItemIdAndEndBeforeAndStatusOrderByEndDesc(
                    item.getId(), LocalDateTime.now(), Status.APPROVED)));
            item.setNextBooking(itemMapper.mapToBookingShortDto(bookingRepository
                    .findFirstByItemIdAndStartAfterAndStatusOrderByStartAsc(
                            item.getId(), LocalDateTime.now(), Status.APPROVED)
            ));
        }
        return ItemListDto.builder().items(personalItems).build();
    }

    @Override
    @Transactional(readOnly = true)
    public ItemListDto getFoundItems(String text) {
        if (text.isBlank()) {
            return ItemListDto.builder().items(new ArrayList<>()).build();
        }
        return ItemListDto.builder()
                .items(itemRepository.findAllByNameOrDescriptionContainingIgnoreCaseAndAvailableTrue(text, text).stream()
                        .map(itemMapper::mapToItemDtoResponse).collect(Collectors.toList())).build();
    }

    @Override
    @Transactional
    public CommentDtoResponse addComment(Long itemId, Long userId, CommentDto commentDto) {
        if (!bookingRepository.existsBookingByItemIdAndBookerIdAndStatusAndEndIsBefore(itemId, userId,
                Status.APPROVED, LocalDateTime.now())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "У пользователя с id="
                    + userId + " небыло ниодной брони на предмет с id=" + itemId);
        } else {
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
    }
}