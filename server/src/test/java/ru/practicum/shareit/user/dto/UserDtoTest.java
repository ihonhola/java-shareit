package ru.practicum.shareit.user.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class UserDtoTest {

    @Test
    void testAllArgsConstructor() {
        UserDto dto = new UserDto(1, "Name", "email@mail.com");
        assertEquals(1, dto.getId());
        assertEquals("Name", dto.getName());
        assertEquals("email@mail.com", dto.getEmail());
    }

    @Test
    void testNoArgsConstructor() {
        UserDto dto = new UserDto();
        assertNull(dto.getId());
        assertNull(dto.getName());
        assertNull(dto.getEmail());
    }
}