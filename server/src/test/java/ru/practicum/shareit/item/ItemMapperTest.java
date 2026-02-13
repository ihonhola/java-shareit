package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ItemMapperTest {

    @Test
    void toItemDto_shouldMapCorrectly() {
        User owner = new User();
        owner.setId(1);

        ItemRequest request = new ItemRequest();
        request.setId(10);

        Item item = new Item();
        item.setId(2);
        item.setName("Гиперболоид");
        item.setDescription("Принадлежал инженеру Гарину");
        item.setAvailable(true);
        item.setOwner(owner);
        item.setRequest(request);

        ItemDto dto = ItemMapper.toItemDto(item);
        assertNotNull(dto);
        assertEquals(2, dto.getId());
        assertEquals("Гиперболоид", dto.getName());
        assertEquals("Принадлежал инженеру Гарину", dto.getDescription());
        assertTrue(dto.getAvailable());
        assertEquals(10, dto.getRequestId());
        // lastBooking, nextBooking, comments должны быть null, т.к. не заполняются в маппере
        assertNull(dto.getLastBooking());
        assertNull(dto.getNextBooking());
        assertNull(dto.getComments());
    }

    @Test
    void toItemDto_shouldReturnNull_whenItemNull() {
        assertNull(ItemMapper.toItemDto(null));
    }

    @Test
    void toItemDto_shouldSetRequestIdNull_whenRequestNull() {
        Item item = new Item();
        item.setRequest(null);
        ItemDto dto = ItemMapper.toItemDto(item);
        assertNull(dto.getRequestId());
    }

    @Test
    void toItem_shouldMapCorrectly() {
        User owner = new User();
        owner.setId(1);

        ItemRequest request = new ItemRequest();
        request.setId(10);

        ItemDto dto = new ItemDto();
        dto.setId(3);
        dto.setName("Молот");
        dto.setDescription("Принадлежал инженеру Тору");
        dto.setAvailable(false);
        dto.setRequestId(10);

        Item item = ItemMapper.toItem(dto, owner, request);
        assertNotNull(item);
        assertEquals(3, item.getId());
        assertEquals("Молот", item.getName());
        assertEquals("Принадлежал инженеру Тору", item.getDescription());
        assertFalse(item.getAvailable());
        assertEquals(owner, item.getOwner());
        assertEquals(request, item.getRequest());
    }

    @Test
    void toItem_shouldReturnNull_whenDtoNull() {
        assertNull(ItemMapper.toItem(null, new User(), new ItemRequest()));
    }

    @Test
    void toItem_shouldSetRequestNull_whenRequestNull() {
        ItemDto dto = new ItemDto();
        dto.setName("Test");
        Item item = ItemMapper.toItem(dto, new User(), null);
        assertNull(item.getRequest());
    }

    @Test
    void updateItemFromDto_shouldUpdateOnlyNonNullFields() {
        Item existingItem = new Item();
        existingItem.setName("Old");
        existingItem.setDescription("Old desc");
        existingItem.setAvailable(false);

        ItemDto updateDto = new ItemDto();
        updateDto.setName("New");
        // description null
        updateDto.setAvailable(true);

        ItemMapper.updateItemFromDto(updateDto, existingItem);

        assertEquals("New", existingItem.getName());
        assertEquals("Old desc", existingItem.getDescription());
        assertTrue(existingItem.getAvailable());
    }

    @Test
    void updateItemFromDto_shouldNotUpdate_whenAllFieldsNull() {
        Item existingItem = new Item();
        existingItem.setName("Old");
        existingItem.setDescription("Old desc");
        existingItem.setAvailable(false);

        ItemDto updateDto = new ItemDto(); // все поля null

        ItemMapper.updateItemFromDto(updateDto, existingItem);

        assertEquals("Old", existingItem.getName());
        assertEquals("Old desc", existingItem.getDescription());
        assertFalse(existingItem.getAvailable());
    }
}