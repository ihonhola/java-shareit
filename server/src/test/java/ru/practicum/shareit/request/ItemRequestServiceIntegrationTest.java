package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.dto.RequestResponseDto;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class ItemRequestServiceIntegrationTest {

    @Autowired
    private ItemRequestService requestService;

    @Autowired
    private UserService userService;

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
    void getOwnRequests_shouldReturnList() {
        UserDto user = userService.createUser(new UserDto(null, "User", "user@mail.com"));
        RequestDto dto = new RequestDto();
        dto.setDescription("Нужен гиперболоид");
        requestService.createRequest(dto, user.getId());

        List<RequestResponseDto> requests = requestService.getOwnRequests(user.getId());
        assertEquals(1, requests.size());
        assertEquals("Нужен гиперболоид", requests.get(0).getDescription());
    }
}