package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.dto.RequestResponseDto;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class ItemRequestServiceIntegrationTest {

    @Autowired
    private ItemRequestService requestService;

    @Autowired
    private UserService userService;

    @Autowired
    private ItemService itemService;

    @Test
    void createRequest_shouldSaveAndReturn() {
        UserDto user = userService.createUser(new UserDto(null, "User", "user@mail.com"));
        RequestDto dto = new RequestDto();
        dto.setDescription("Нужен гиперболоид");

        RequestResponseDto response = requestService.createRequest(dto, user.getId());

        assertNotNull(response.getId());
        assertEquals("Нужен гиперболоид", response.getDescription());
        assertNotNull(response.getCreated());
    }

    @Test
    void createRequest_userNotFound_shouldThrowNotFound() {
        RequestDto dto = new RequestDto();
        dto.setDescription("Need a drill");
        assertThrows(NotFoundException.class, () -> requestService.createRequest(dto, 999));
    }

    @Test
    void getOwnRequests_shouldReturnList() {
        UserDto user = userService.createUser(new UserDto(null, "User", "user@mail.com"));
        RequestDto dto = new RequestDto();
        dto.setDescription("Нужен гиперболоид");
        requestService.createRequest(dto, user.getId());

        List<RequestResponseDto> requests = requestService.getOwnRequests(user.getId());
        assertEquals(1, requests.size());
        assertEquals("Нужен гиперболоид", requests.get(0).getDescription());
    }

    @Test
    void getOwnRequests_userNotFound_shouldThrowNotFound() {
        assertThrows(NotFoundException.class, () -> requestService.getOwnRequests(999));
    }

    @Test
    void getAllOtherUsersRequests_shouldReturnList() {
        UserDto user1 = userService.createUser(new UserDto(null, "User1", "user1@mail.com"));
        UserDto user2 = userService.createUser(new UserDto(null, "User2", "user2@mail.com"));

        RequestDto dto = new RequestDto();
        dto.setDescription("Need a drill");
        requestService.createRequest(dto, user1.getId());

        List<RequestResponseDto> requests = requestService.getAllOtherUsersRequests(user2.getId(), 0, 10);
        assertEquals(1, requests.size());
        assertEquals("Need a drill", requests.get(0).getDescription());
    }

    @Test
    void getAllOtherUsersRequests_userNotFound_shouldThrowNotFound() {
        assertThrows(NotFoundException.class, () -> requestService.getAllOtherUsersRequests(999, 0, 10));
    }

    @Test
    void getRequestById_shouldReturnWithItems() {
        UserDto requestor = userService.createUser(new UserDto(null, "Requestor", "req@mail.com"));
        UserDto owner = userService.createUser(new UserDto(null, "Owner", "owner@mail.com"));

        RequestDto dto = new RequestDto();
        dto.setDescription("Need a drill");
        RequestResponseDto created = requestService.createRequest(dto, requestor.getId());

        // Создаём вещь в ответ на запрос
        ItemDto itemDto = new ItemDto(null, "Drill", "Power tool", true,
                created.getId(), null, null, null);
        itemService.createItem(itemDto, owner.getId());

        RequestResponseDto found = requestService.getRequestById(created.getId(), owner.getId());
        assertEquals(created.getId(), found.getId());
        assertEquals(1, found.getItems().size());
        assertEquals("Drill", found.getItems().get(0).getName());
    }

    @Test
    void getRequestById_notFound_shouldThrowNotFound() {
        UserDto user = userService.createUser(new UserDto(null, "User", "user@mail.com"));
        assertThrows(NotFoundException.class, () -> requestService.getRequestById(999, user.getId()));
    }

    @Test
    void getRequestById_userNotFound_shouldThrowNotFound() {
        UserDto requestor = userService.createUser(new UserDto(null, "Requestor", "req@mail.com"));
        RequestDto dto = new RequestDto();
        dto.setDescription("Need a drill");
        RequestResponseDto created = requestService.createRequest(dto, requestor.getId());

        assertThrows(NotFoundException.class, () -> requestService.getRequestById(created.getId(), 999));
    }
}