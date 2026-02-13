package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.dto.RequestResponseDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ItemRequestMapperTest {

    @Test
    void toItemRequest_shouldMapCorrectly() {
        RequestDto requestDto = new RequestDto();
        requestDto.setDescription("Нужен гиперболоид");

        User requestor = new User();
        requestor.setId(1);

        ItemRequest itemRequest = ItemRequestMapper.toItemRequest(requestDto, requestor);

        assertNotNull(itemRequest);
        assertEquals("Нужен гиперболоид", itemRequest.getDescription());
        assertEquals(requestor, itemRequest.getRequestor());
        assertNotNull(itemRequest.getCreated());
        assertTrue(itemRequest.getCreated().isBefore(LocalDateTime.now().plusSeconds(1)));
    }

    @Test
    void toRequestResponseDto_shouldMapCorrectly_withItems() {
        ItemRequest request = new ItemRequest();
        request.setId(1);
        request.setDescription("Нужен гиперболоид");
        request.setCreated(LocalDateTime.now());

        User requestor = new User();
        requestor.setId(1);
        request.setRequestor(requestor);

        Item item = new Item();
        item.setId(10);
        item.setName("Гиперболоид");
        item.setDescription("Принадлежал инженеру Гарину");
        item.setAvailable(true);
        item.setOwner(requestor);
        item.setRequest(request);
        request.setItems(List.of(item));

        RequestResponseDto dto = ItemRequestMapper.toRequestResponseDto(request);

        assertNotNull(dto);
        assertEquals(1, dto.getId());
        assertEquals("Нужен гиперболоид", dto.getDescription());
        assertEquals(request.getCreated(), dto.getCreated());
        assertNotNull(dto.getItems());
        assertEquals(1, dto.getItems().size());

        ItemRequestDto itemDto = dto.getItems().get(0);
        assertEquals(10, itemDto.getId());
        assertEquals("Гиперболоид", itemDto.getName());
        assertEquals("Принадлежал инженеру Гарину", itemDto.getDescription());
        assertTrue(itemDto.getAvailable());
        assertEquals(1, itemDto.getOwnerId());
        assertEquals(1, itemDto.getRequestId());
    }

    @Test
    void toRequestResponseDto_shouldMapCorrectly_withoutItems() {
        ItemRequest request = new ItemRequest();
        request.setId(1);
        request.setDescription("Нужен гиперболоид");
        request.setCreated(LocalDateTime.now());
        request.setItems(null); // или пустой список

        RequestResponseDto dto = ItemRequestMapper.toRequestResponseDto(request);

        assertNotNull(dto);
        assertEquals(1, dto.getId());
        assertEquals("Нужен гиперболоид", dto.getDescription());
        assertNotNull(dto.getCreated());
        assertNotNull(dto.getItems());
        assertTrue(dto.getItems().isEmpty());
    }

    @Test
    void toRequestResponseDto_shouldReturnNull_whenItemRequestNull() {
        assertNull(ItemRequestMapper.toRequestResponseDto(null));
    }

    @Test
    void toItemForRequestDto_shouldMapCorrectly() {
        Item item = new Item();
        item.setId(5);
        item.setName("Молот");
        item.setDescription("Принадлежал инженеру Тору");
        item.setAvailable(true);

        User owner = new User();
        owner.setId(2);
        item.setOwner(owner);

        ItemRequest request = new ItemRequest();
        request.setId(3);
        item.setRequest(request);

        ItemRequestDto dto = ItemRequestMapper.toItemForRequestDto(item);

        assertNotNull(dto);
        assertEquals(5, dto.getId());
        assertEquals("Молот", dto.getName());
        assertEquals("Принадлежал инженеру Тору", dto.getDescription());
        assertTrue(dto.getAvailable());
        assertEquals(2, dto.getOwnerId());
        assertEquals(3, dto.getRequestId());
    }

    @Test
    void toItemForRequestDto_shouldReturnNull_whenItemNull() {
        assertNull(ItemRequestMapper.toItemForRequestDto(null));
    }
}