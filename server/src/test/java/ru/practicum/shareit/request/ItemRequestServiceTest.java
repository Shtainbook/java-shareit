package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoResponse;
import ru.practicum.shareit.request.dto.RequestDtoResponseWithMD;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase
@ActiveProfiles("test")
@Sql(scripts = {"file:src/main/resources/schema.sql"})
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ItemRequestServiceTest {
    private final ItemRequestService itemRequestService;
    private final UserRepository userRepository;
    private User user1;
    private User user2;
    private ItemRequestDto itemRequestDto;

    @BeforeEach
    public void setUp() {
        user1 = new User();
        user1.setName("test name");
        user1.setEmail("test@test.ru");
        user2 = new User();
        user2.setName("test name2");
        user2.setEmail("test2@test.ru");
        itemRequestDto = ItemRequestDto.builder()
                .description("test request description")
                .build();
    }

    @Test
    public void createInfoRequest() {
        //given
        userRepository.save(user1);
        //when
        var savedRequest = itemRequestService.createItemRequest(itemRequestDto, user1.getId());
        var findRequest = itemRequestService.getInfoRequest(user1.getId(), savedRequest.getId());
        //then
        assertThat(savedRequest).usingRecursiveComparison().ignoringFields("items", "created")
                .isEqualTo(findRequest);
    }

    @Test
    public void createItemRequestWhenRequesterNotFound() {
        //given
        userRepository.save(user1);
        assertThatThrownBy(
                //when
                () -> itemRequestService.createItemRequest(itemRequestDto, 99L)
                //then
        ).isInstanceOf(ResponseStatusException.class);
    }

    @Test
    public void getUserRequest() {
        //given
        userRepository.save(user1);
        userRepository.save(user2);
        ItemRequestDtoResponse savedRequest = itemRequestService.createItemRequest(itemRequestDto, user2.getId());
        //when
        List<RequestDtoResponseWithMD> privateRequests = itemRequestService
                .getUserRequests(PageRequest.of(0, 2), user2.getId());
        RequestDtoResponseWithMD findRequest = itemRequestService.getInfoRequest(user2.getId(), savedRequest.getId());
        //then
        assertThat(privateRequests.get(0)).usingRecursiveComparison().isEqualTo(findRequest);
    }

    @Test
    public void getUserRequestWhenRequesterNotExistingRequests() {
        //given
        userRepository.save(user1);
        assertThatThrownBy(
                //when
                () -> itemRequestService
                        .getUserRequests(PageRequest.of(0, 2), 55L)
                //then
        ).isInstanceOf(ResponseStatusException.class);
    }

    @Test
    public void getDifferentRequests() {
        //given
        userRepository.save(user1);
        userRepository.save(user2);
        ItemRequestDtoResponse savedRequest = itemRequestService.createItemRequest(itemRequestDto, user1.getId());
        RequestDtoResponseWithMD findRequest = itemRequestService.getInfoRequest(user1.getId(), savedRequest.getId());
        //when
        List<RequestDtoResponseWithMD> otherRequest = itemRequestService.getDifferentRequests(PageRequest.of(0, 2), user2.getId());
        //then
        assertThat(otherRequest.get(0)).usingRecursiveComparison().isEqualTo(findRequest);
    }

    @Test
    public void getDeifferentRequestsWhenRequesterNotFound() {
        //given
        userRepository.save(user1);
        itemRequestService.createItemRequest(itemRequestDto, user1.getId());
        assertThatThrownBy(
                //when
                () -> itemRequestService.getDifferentRequests(PageRequest.of(0, 2), 50L)
                //then
        ).isInstanceOf(ResponseStatusException.class);
    }

    @Test
    public void getInfoRequestWhenUserNotFound() {
        //given
        userRepository.save(user1);
        ItemRequestDtoResponse savedRequest = itemRequestService.createItemRequest(itemRequestDto, user1.getId());
        assertThatThrownBy(
                //when
                () -> itemRequestService.getInfoRequest(50L, savedRequest.getId())
                //then
        ).isInstanceOf(ResponseStatusException.class);
    }

    @Test
    public void getInfoRequestWhenRequestNotFound() {
        //given
        userRepository.save(user1);
        ItemRequestDtoResponse savedRequest = itemRequestService.createItemRequest(itemRequestDto, user1.getId());
        assertThatThrownBy(
                //when
                () -> itemRequestService.getInfoRequest(savedRequest.getId(), 50L)
                //then
        ).isInstanceOf(ResponseStatusException.class);
    }
}
