package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.UserService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class ItemServiceIntegrationTest {

    @Autowired
    private ItemService itemService;

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Test
    void createItem_shouldSaveAndReturnItem() {
        UserDto owner = userService.createUser(new UserDto(null, "Owner", "owner@mail.com"));
        ItemDto itemDto = new ItemDto(null, "Drill", "Power tool", true, null,
                null, null, null);
        ItemDto saved = itemService.createItem(itemDto, owner.getId());

        assertNotNull(saved.getId());
        assertEquals("Drill", saved.getName());
        assertTrue(saved.getAvailable());
    }

    @Test
    void getItemById_asOwner_shouldContainBookings() {
        UserDto owner = userService.createUser(new UserDto(null, "Owner", "owner@mail.com"));
        ItemDto itemDto = new ItemDto(null, "Гиперболоид", "Принадлежал инженеру Гарину",
                true, null, null, null, null);
        ItemDto saved = itemService.createItem(itemDto, owner.getId());

        ItemDto found = itemService.getItemById(saved.getId(), owner.getId());
        assertEquals(saved.getId(), found.getId());
        assertNotNull(found);
    }

    @Test
    void searchAvailableItems_byText_shouldReturnMatching() {
        UserDto owner = userService.createUser(new UserDto(null, "Owner", "owner@mail.com"));
        ItemDto drill = new ItemDto(null, "Гиперболоид", "Принадлежал инженеру Гарину",
                true, null, null, null, null);
        ItemDto hammer = new ItemDto(null, "Молот", "Принадлежал инженеру Тору",
                true, null, null, null, null);
        itemService.createItem(drill, owner.getId());
        itemService.createItem(hammer, owner.getId());

        var result = itemService.searchAvailableItems("инженер");
        assertEquals(2, result.size());
    }
}