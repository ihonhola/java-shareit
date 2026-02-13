package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exceptions.ConflictException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class UserServiceIntegrationTest {

    @Autowired
    private UserService userService;

    @Test
    void createUser_shouldSaveAndReturnUser() {
        UserDto dto = new UserDto(null, "John", "john@mail.com");
        UserDto saved = userService.createUser(dto);

        assertNotNull(saved.getId());
        assertEquals("John", saved.getName());
        assertEquals("john@mail.com", saved.getEmail());
    }

    @Test
    void createUser_duplicateEmail_shouldThrowConflict() {
        UserDto dto = new UserDto(null, "John", "john@mail.com");
        userService.createUser(dto);

        UserDto duplicate = new UserDto(null, "Johnny", "john@mail.com");
        assertThrows(ConflictException.class, () -> userService.createUser(duplicate));
    }

    @Test
    void getUserById_existingId_shouldReturnUser() {
        UserDto created = userService.createUser(new UserDto(null, "Jane", "jane@mail.com"));
        UserDto found = userService.getUserById(created.getId());

        assertEquals(created.getId(), found.getId());
        assertEquals("Jane", found.getName());
    }

    @Test
    void getUserById_notExisting_shouldThrowNotFound() {
        assertThrows(NotFoundException.class, () -> userService.getUserById(9999));
    }
}