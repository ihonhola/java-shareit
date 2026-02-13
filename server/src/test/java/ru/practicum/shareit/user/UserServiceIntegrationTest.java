package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exceptions.ConflictException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class UserServiceIntegrationTest {

    @Autowired
    private UserService userService;

    @Autowired
    private ItemService itemService;

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
    void updateUser_shouldUpdateFields() {
        UserDto created = userService.createUser(new UserDto(null, "Old", "old@mail.com"));
        UserDto updateDto = new UserDto(null, "New", "new@mail.com");
        UserDto updated = userService.updateUser(created.getId(), updateDto);

        assertEquals("New", updated.getName());
        assertEquals("new@mail.com", updated.getEmail());
    }

    @Test
    void updateUser_partialUpdate_shouldUpdateOnlyProvidedFields() {
        UserDto created = userService.createUser(new UserDto(null, "Old", "old@mail.com"));
        UserDto updateDto = new UserDto(null, "New", null);
        UserDto updated = userService.updateUser(created.getId(), updateDto);

        assertEquals("New", updated.getName());
        assertEquals("old@mail.com", updated.getEmail());
    }

    @Test
    void updateUser_conflictEmail_shouldThrowConflict() {
        UserDto user1 = userService.createUser(new UserDto(null, "User1", "user1@mail.com"));
        UserDto user2 = userService.createUser(new UserDto(null, "User2", "user2@mail.com"));

        UserDto updateDto = new UserDto(null, "User2Updated", "user1@mail.com");
        assertThrows(ConflictException.class, () -> userService.updateUser(user2.getId(), updateDto));
    }

    @Test
    void updateUser_notFound_shouldThrowNotFound() {
        UserDto updateDto = new UserDto(null, "New", "new@mail.com");
        assertThrows(NotFoundException.class, () -> userService.updateUser(999, updateDto));
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

    @Test
    void getAllUsers_shouldReturnList() {
        userService.createUser(new UserDto(null, "A", "a@mail.com"));
        userService.createUser(new UserDto(null, "B", "b@mail.com"));

        List<UserDto> all = userService.getAllUsers();
        assertEquals(2, all.size());
    }

    @Test
    void deleteUser_shouldRemoveUser() {
        UserDto created = userService.createUser(new UserDto(null, "ToDelete", "delete@mail.com"));
        userService.deleteUser(created.getId());

        assertThrows(NotFoundException.class, () -> userService.getUserById(created.getId()));
    }

    @Test
    void deleteUser_notExisting_shouldNotThrow() {
        // delete не выбрасывает исключение, если пользователя нет
        assertDoesNotThrow(() -> userService.deleteUser(9999));
    }

    @Test
    void getAllItemsByOwner_noItems_shouldReturnEmptyList() {
        UserDto owner = userService.createUser(new UserDto(null, "Owner", "owner@mail.com"));
        List<ItemDto> items = itemService.getAllItemsByOwner(owner.getId());
        assertTrue(items.isEmpty());
    }
}