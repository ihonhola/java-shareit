package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class UserMapperTest {

    @Test
    void toUserDto_shouldMapCorrectly() {
        User user = new User();
        user.setId(1);
        user.setName("John");
        user.setEmail("john@example.com");

        UserDto dto = UserMapper.toUserDto(user);
        assertNotNull(dto);
        assertEquals(1, dto.getId());
        assertEquals("John", dto.getName());
        assertEquals("john@example.com", dto.getEmail());
    }

    @Test
    void toUserDto_shouldReturnNull_whenUserNull() {
        assertNull(UserMapper.toUserDto(null));
    }

    @Test
    void toUser_shouldMapCorrectly() {
        UserDto dto = new UserDto(2, "Jane", "jane@example.com");

        User user = UserMapper.toUser(dto);
        assertNotNull(user);
        assertEquals(2, user.getId());
        assertEquals("Jane", user.getName());
        assertEquals("jane@example.com", user.getEmail());
    }

    @Test
    void toUser_shouldReturnNull_whenDtoNull() {
        assertNull(UserMapper.toUser(null));
    }

    @Test
    void updateUserFromDto_shouldUpdateOnlyNonNullFields() {
        User existingUser = new User();
        existingUser.setName("Old");
        existingUser.setEmail("old@example.com");

        UserDto updateDto = new UserDto();
        updateDto.setName("New");
        // email null

        UserMapper.updateUserFromDto(updateDto, existingUser);

        assertEquals("New", existingUser.getName());
        assertEquals("old@example.com", existingUser.getEmail());
    }

    @Test
    void updateUserFromDto_shouldNotUpdate_whenAllFieldsNull() {
        User existingUser = new User();
        existingUser.setName("Old");
        existingUser.setEmail("old@example.com");

        UserDto updateDto = new UserDto(); // все поля null

        UserMapper.updateUserFromDto(updateDto, existingUser);

        assertEquals("Old", existingUser.getName());
        assertEquals("old@example.com", existingUser.getEmail());
    }
}