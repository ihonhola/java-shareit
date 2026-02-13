package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class BookingServiceIntegrationTest {

    @Autowired
    private BookingService bookingService;

    @Autowired
    private UserService userService;

    @Autowired
    private ItemService itemService;

    @Test
    void createBooking_shouldSaveAndReturnBooking() {
        UserDto owner = userService.createUser(new UserDto(null, "Owner", "owner@mail.com"));
        UserDto booker = userService.createUser(new UserDto(null, "Booker", "booker@mail.com"));

        ItemDto itemDto = new ItemDto(null, "Гиперболоид", "Принадлежал инженеру Гарину",
                true, null, null, null, null);
        ItemDto savedItem = itemService.createItem(itemDto, owner.getId());

        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(2);
        BookingRequestDto request = new BookingRequestDto();
        request.setStart(start);
        request.setEnd(end);
        request.setItemId(savedItem.getId());

        BookingResponseDto booking = bookingService.createBooking(request, booker.getId());
        assertNotNull(booking.getId());
        assertEquals(BookingStatus.WAITING.name(), booking.getStatus());
        assertEquals(booker.getId(), booking.getBookerId());
        assertEquals(savedItem.getId(), booking.getItemId());
    }

    @Test
    void approveBooking_shouldChangeStatus() {
        UserDto owner = userService.createUser(new UserDto(null, "Owner", "owner@mail.com"));
        UserDto booker = userService.createUser(new UserDto(null, "Booker", "booker@mail.com"));
        ItemDto itemDto = new ItemDto(null, "Гиперболоид", "Принадлежал инженеру Гарину",
                true, null, null, null, null);
        ItemDto savedItem = itemService.createItem(itemDto, owner.getId());

        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(2);
        BookingRequestDto request = new BookingRequestDto();
        request.setStart(start);
        request.setEnd(end);
        request.setItemId(savedItem.getId());

        BookingResponseDto created = bookingService.createBooking(request, booker.getId());
        BookingResponseDto approved = bookingService.approveBooking(created.getId(), owner.getId(), true);

        assertEquals(BookingStatus.APPROVED.name(), approved.getStatus());
    }
}