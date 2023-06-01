package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.dto.ItemDataForRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoResponse;
import ru.practicum.shareit.request.dto.RequestDtoResponseWithMD;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ItemRequestControllerTest {
    private final ObjectMapper objectMapper;
    private final MockMvc mvc;
    @MockBean
    private final ItemRequestService itemRequestService;
    private ItemRequestDto itemRequestDto;
    private ItemRequestDtoResponse itemRequestDtoResponse;
    private List<RequestDtoResponseWithMD> itemRequestListDto;
    private RequestDtoResponseWithMD requestDtoResponseWithMD;
    private ItemDataForRequestDto itemDataForRequestDto;

    @BeforeEach
    public void setUp() {
        itemRequestDto = ItemRequestDto.builder()
                .description("test description")
                .build();
        itemRequestDtoResponse = ItemRequestDtoResponse.builder()
                .id(1L)
                .description(itemRequestDto.getDescription())
                .created(LocalDateTime.now())
                .build();
        requestDtoResponseWithMD = RequestDtoResponseWithMD.builder()
                .id(1L)
                .description(itemRequestDtoResponse.getDescription())
                .created(itemRequestDtoResponse.getCreated())
                .build();
        itemDataForRequestDto = ItemDataForRequestDto.builder()
                .id(1L)
                .name("test item name")
                .description("test description name")
                .requestId(1L)
                .available(Boolean.TRUE)
                .build();
    }

    @Test
    @SneakyThrows
    public void createRequest() {
        //when
        when(itemRequestService.createItemRequest(any(ItemRequestDto.class), anyLong()))
                .thenReturn(itemRequestDtoResponse);
        mvc.perform(
                        post("/requests")
                                .header("X-Sharer-User-Id", 1)
                                .content(objectMapper.writeValueAsString(itemRequestDto))
                                .contentType(MediaType.APPLICATION_JSON)
                ).andDo(print())
                .andExpectAll(
                        //then
                        status().isOk(),
                        content().json(objectMapper.writeValueAsString(itemRequestDtoResponse))
                );
    }

    @Test
    @SneakyThrows
    public void getUserRequests() {
        //given
        requestDtoResponseWithMD.setItems(Set.of(itemDataForRequestDto));
        itemRequestListDto = List.of(requestDtoResponseWithMD);
        //when
        when(itemRequestService.getUserRequests(any(PageRequest.class), anyLong())).thenReturn(itemRequestListDto);
        mvc.perform(
                        get("/requests")
                                .header("X-Sharer-User-Id", 1)
                                .param("from", "0")
                                .param("size", "2")
                ).andDo(print())
                .andExpectAll(
                        status().isOk(),
                        content().json(objectMapper.writeValueAsString(itemRequestListDto))
                );
    }

    @Test
    @SneakyThrows
    public void getDifferentRequests() {
        //given
        requestDtoResponseWithMD.setItems(Set.of(itemDataForRequestDto));
        itemRequestListDto = List.of(requestDtoResponseWithMD);
        //when
        when(itemRequestService.getDifferentRequests(any(PageRequest.class), anyLong())).thenReturn(itemRequestListDto);
        mvc.perform(
                        get("/requests/all")
                                .header("X-Sharer-User-Id", 1)
                                .param("from", "0")
                                .param("size", "2")
                ).andDo(print())
                .andExpectAll(
                        status().isOk(),
                        content().json(objectMapper.writeValueAsString(itemRequestListDto))
                );
    }

    @Test
    @SneakyThrows
    public void getInfoRequest() {
        //given
        requestDtoResponseWithMD.setItems(Set.of(itemDataForRequestDto));
        //when
        when(itemRequestService.getInfoRequest(anyLong(), anyLong())).thenReturn(requestDtoResponseWithMD);
        mvc.perform(
                        get("/requests/1")
                                .header("X-Sharer-User-Id", 1)
                ).andDo(print())
                .andExpectAll(
                        status().isOk(),
                        content().json(objectMapper.writeValueAsString(requestDtoResponseWithMD))
                );
    }

}
