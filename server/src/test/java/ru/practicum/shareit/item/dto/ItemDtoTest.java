package ru.practicum.shareit.item.dto;

import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class ItemDtoTest {

    @Test
    void testAllArgsConstructor() {
        ItemDto dto = new ItemDto(1, "Гиперболоид", "Принадлежал инженеру Гарину", true, 5, null, null, List.of());
        assertEquals(1, dto.getId());
        assertEquals("Гиперболоид", dto.getName());
        assertEquals("Принадлежал инженеру Гарину", dto.getDescription());
        assertTrue(dto.getAvailable());
        assertEquals(5, dto.getRequestId());
    }

    @Test
    void testNoArgsConstructor() {
        ItemDto dto = new ItemDto();
        assertNull(dto.getId());
        assertNull(dto.getName());
    }
}