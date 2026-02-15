package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.exceptions.AccessDeniedException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.exceptions.NotFoundException;

import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

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

    private UserDto createOwner() {
        return userService.createUser(new UserDto(null, "Owner", "owner" +
                System.currentTimeMillis() + "@mail.com"));
    }

    private UserDto createBooker() {
        return userService.createUser(new UserDto(null, "Booker", "booker" +
                System.currentTimeMillis() + "@mail.com"));
    }

    private ItemDto createItem(UserDto owner) {
        return itemService.createItem(
                new ItemDto(null, "Гиперболоид", "Принадлежал инженеру Гарину",
                        true, null, null, null, null),
                owner.getId()
        );
    }

    @Test
    void createBooking_shouldSaveAndReturn() {
        UserDto owner = createOwner();
        UserDto booker = createBooker();
        ItemDto item = createItem(owner);

        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(2);
        BookingRequestDto request = new BookingRequestDto();
        request.setItemId(item.getId());
        request.setStart(start);
        request.setEnd(end);

        BookingResponseDto created = bookingService.createBooking(request, booker.getId());

        assertNotNull(created.getId());
        assertEquals(BookingStatus.WAITING.name(), created.getStatus());
        assertEquals(booker.getId(), created.getBookerId());
        assertEquals(item.getId(), created.getItemId());
    }

    @Test
    void createBooking_itemNotAvailable_shouldThrowValidation() {
        UserDto owner = createOwner();
        UserDto booker = createBooker();
        ItemDto item = itemService.createItem(
                new ItemDto(null, "Гиперболоид", "Принадлежал инженеру Гарину", false, null,
                        null, null, null),
                owner.getId()
        );

        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(2);
        BookingRequestDto request = new BookingRequestDto();
        request.setItemId(item.getId());
        request.setStart(start);
        request.setEnd(end);

        assertThrows(ValidationException.class, () -> bookingService.createBooking(request, booker.getId()));
    }

    @Test
    void createBooking_ownerCannotBookOwnItem_shouldThrowValidation() {
        UserDto owner = createOwner();
        ItemDto item = createItem(owner);

        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(2);
        BookingRequestDto request = new BookingRequestDto();
        request.setItemId(item.getId());
        request.setStart(start);
        request.setEnd(end);

        assertThrows(ValidationException.class, () -> bookingService.createBooking(request, owner.getId()));
    }

    @Test
    void createBooking_endBeforeStart_shouldThrowValidation() {
        UserDto owner = createOwner();
        UserDto booker = createBooker();
        ItemDto item = createItem(owner);

        LocalDateTime start = LocalDateTime.now().plusDays(2);
        LocalDateTime end = LocalDateTime.now().plusDays(1);
        BookingRequestDto request = new BookingRequestDto();
        request.setItemId(item.getId());
        request.setStart(start);
        request.setEnd(end);

        assertThrows(ValidationException.class, () -> bookingService.createBooking(request, booker.getId()));
    }

    @Test
    void createBooking_startEqualsEnd_shouldThrowValidation() {
        UserDto owner = createOwner();
        UserDto booker = createBooker();
        ItemDto item = createItem(owner);

        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = start;
        BookingRequestDto request = new BookingRequestDto();
        request.setItemId(item.getId());
        request.setStart(start);
        request.setEnd(end);

        assertThrows(ValidationException.class, () -> bookingService.createBooking(request, booker.getId()));
    }

    @Test
    void createBooking_itemAlreadyBooked_shouldThrowValidation() {
        UserDto owner = createOwner();
        UserDto booker1 = createBooker();
        UserDto booker2 = createBooker();
        ItemDto item = createItem(owner);

        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(2);
        BookingRequestDto request1 = new BookingRequestDto();
        request1.setItemId(item.getId());
        request1.setStart(start);
        request1.setEnd(end);
        bookingService.createBooking(request1, booker1.getId());

        BookingRequestDto request2 = new BookingRequestDto();
        request2.setItemId(item.getId());
        request2.setStart(start);
        request2.setEnd(end);
        assertThrows(ValidationException.class, () -> bookingService.createBooking(request2, booker2.getId()));
    }

    @Test
    void approveBooking_shouldChangeStatus() {
        UserDto owner = createOwner();
        UserDto booker = createBooker();
        ItemDto item = createItem(owner);

        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(2);
        BookingRequestDto request = new BookingRequestDto();
        request.setItemId(item.getId());
        request.setStart(start);
        request.setEnd(end);

        BookingResponseDto created = bookingService.createBooking(request, booker.getId());

        BookingResponseDto approved = bookingService.approveBooking(created.getId(), owner.getId(), true);
        assertEquals(BookingStatus.APPROVED.name(), approved.getStatus());
    }

    @Test
    void approveBooking_reject_shouldChangeStatus() {
        UserDto owner = createOwner();
        UserDto booker = createBooker();
        ItemDto item = createItem(owner);

        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(2);
        BookingRequestDto request = new BookingRequestDto();
        request.setItemId(item.getId());
        request.setStart(start);
        request.setEnd(end);

        BookingResponseDto created = bookingService.createBooking(request, booker.getId());

        BookingResponseDto rejected = bookingService.approveBooking(created.getId(), owner.getId(),
                false);
        assertEquals(BookingStatus.REJECTED.name(), rejected.getStatus());
    }

    @Test
    void approveBooking_notOwner_shouldThrowAccessDenied() {
        UserDto owner = createOwner();
        UserDto booker = createBooker();
        UserDto other = createOwner(); // другой пользователь
        ItemDto item = createItem(owner);

        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(2);
        BookingRequestDto request = new BookingRequestDto();
        request.setItemId(item.getId());
        request.setStart(start);
        request.setEnd(end);

        BookingResponseDto created = bookingService.createBooking(request, booker.getId());

        assertThrows(AccessDeniedException.class, () -> bookingService.approveBooking(created.getId(),
                other.getId(), true));
    }

    @Test
    void approveBooking_alreadyApproved_shouldThrowValidation() {
        UserDto owner = createOwner();
        UserDto booker = createBooker();
        ItemDto item = createItem(owner);

        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(2);
        BookingRequestDto request = new BookingRequestDto();
        request.setItemId(item.getId());
        request.setStart(start);
        request.setEnd(end);

        BookingResponseDto created = bookingService.createBooking(request, booker.getId());
        bookingService.approveBooking(created.getId(), owner.getId(), true);

        assertThrows(ValidationException.class, () ->
                bookingService.approveBooking(created.getId(), owner.getId(), true));
    }

    @Test
    void getBookingById_asOwner_shouldReturn() {
        UserDto owner = createOwner();
        UserDto booker = createBooker();
        ItemDto item = createItem(owner);

        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(2);
        BookingRequestDto request = new BookingRequestDto();
        request.setItemId(item.getId());
        request.setStart(start);
        request.setEnd(end);

        BookingResponseDto created = bookingService.createBooking(request, booker.getId());

        BookingResponseDto found = bookingService.getBookingById(created.getId(), owner.getId());
        assertEquals(created.getId(), found.getId());
    }

    @Test
    void getBookingById_asBooker_shouldReturn() {
        UserDto owner = createOwner();
        UserDto booker = createBooker();
        ItemDto item = createItem(owner);

        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(2);
        BookingRequestDto request = new BookingRequestDto();
        request.setItemId(item.getId());
        request.setStart(start);
        request.setEnd(end);

        BookingResponseDto created = bookingService.createBooking(request, booker.getId());

        BookingResponseDto found = bookingService.getBookingById(created.getId(), booker.getId());
        assertEquals(created.getId(), found.getId());
    }

    @Test
    void getBookingById_asOther_shouldThrowAccessDenied() {
        UserDto owner = createOwner();
        UserDto booker = createBooker();
        UserDto other = createOwner();
        ItemDto item = createItem(owner);

        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(2);
        BookingRequestDto request = new BookingRequestDto();
        request.setItemId(item.getId());
        request.setStart(start);
        request.setEnd(end);

        BookingResponseDto created = bookingService.createBooking(request, booker.getId());

        assertThrows(AccessDeniedException.class, () ->
                bookingService.getBookingById(created.getId(), other.getId()));
    }

    @Test
    void getUserBookings_withAllState_shouldReturnList() {
        UserDto owner = createOwner();
        UserDto booker = createBooker();
        ItemDto item = createItem(owner);

        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(2);
        BookingRequestDto request = new BookingRequestDto();
        request.setItemId(item.getId());
        request.setStart(start);
        request.setEnd(end);

        bookingService.createBooking(request, booker.getId());

        List<BookingResponseDto> bookings =
                bookingService.getUserBookings(booker.getId(), BookingState.ALL, 0, 10);
        assertEquals(1, bookings.size());
    }

    @Test
    void getUserBookings_withWaitingState_shouldReturnWaiting() {
        UserDto owner = createOwner();
        UserDto booker = createBooker();
        ItemDto item = createItem(owner);

        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(2);
        BookingRequestDto request = new BookingRequestDto();
        request.setItemId(item.getId());
        request.setStart(start);
        request.setEnd(end);

        bookingService.createBooking(request, booker.getId());

        List<BookingResponseDto> bookings =
                bookingService.getUserBookings(booker.getId(), BookingState.WAITING, 0, 10);
        assertEquals(1, bookings.size());
    }

    @Test
    void getUserBookings_withRejectedState_shouldReturnEmpty() {
        UserDto owner = createOwner();
        UserDto booker = createBooker();
        ItemDto item = createItem(owner);

        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(2);
        BookingRequestDto request = new BookingRequestDto();
        request.setItemId(item.getId());
        request.setStart(start);
        request.setEnd(end);

        bookingService.createBooking(request, booker.getId());

        List<BookingResponseDto> bookings =
                bookingService.getUserBookings(booker.getId(), BookingState.REJECTED, 0, 10);
        assertTrue(bookings.isEmpty());
    }

    @Test
    void getUserBookings_userNotFound_shouldThrowNotFound() {
        assertThrows(NotFoundException.class, () ->
                bookingService.getUserBookings(999, BookingState.ALL, 0, 10));
    }

    @Test
    void getOwnerBookings_withAllState_shouldReturnList() {
        UserDto owner = createOwner();
        UserDto booker = createBooker();
        ItemDto item = createItem(owner);

        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(2);
        BookingRequestDto request = new BookingRequestDto();
        request.setItemId(item.getId());
        request.setStart(start);
        request.setEnd(end);

        bookingService.createBooking(request, booker.getId());

        List<BookingResponseDto> bookings =
                bookingService.getOwnerBookings(owner.getId(), BookingState.ALL, 0, 10);
        assertEquals(1, bookings.size());
    }

    @Test
    void getOwnerBookings_userHasNoItems_shouldThrowValidation() {
        UserDto owner = createOwner(); // нет вещей
        assertThrows(ValidationException.class, () ->
                bookingService.getOwnerBookings(owner.getId(), BookingState.ALL, 0, 10));
    }

    @Test
    void getUserBookings_withCurrentState_shouldReturnCurrentBookings() {
        UserDto owner = createOwner();
        UserDto booker = createBooker();
        ItemDto item = createItem(owner);

        LocalDateTime start = LocalDateTime.now().minusHours(1);
        LocalDateTime end = LocalDateTime.now().plusHours(1);
        BookingRequestDto request = new BookingRequestDto();
        request.setItemId(item.getId());
        request.setStart(start);
        request.setEnd(end);
        bookingService.createBooking(request, booker.getId());

        List<BookingResponseDto> bookings = bookingService.getUserBookings(booker.getId(), BookingState.CURRENT, 0, 10);
        assertEquals(1, bookings.size());
    }

    @Test
    void getUserBookings_withPastState_shouldReturnPastBookings() {
        UserDto owner = createOwner();
        UserDto booker = createBooker();
        ItemDto item = createItem(owner);

        LocalDateTime start = LocalDateTime.now().minusDays(2);
        LocalDateTime end = LocalDateTime.now().minusDays(1);
        BookingRequestDto request = new BookingRequestDto();
        request.setItemId(item.getId());
        request.setStart(start);
        request.setEnd(end);
        bookingService.createBooking(request, booker.getId());

        List<BookingResponseDto> bookings = bookingService.getUserBookings(booker.getId(),
                BookingState.PAST, 0, 10);
        assertEquals(1, bookings.size());
    }

    @Test
    void getUserBookings_withFutureState_shouldReturnFutureBookings() {
        UserDto owner = createOwner();
        UserDto booker = createBooker();
        ItemDto item = createItem(owner);

        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(2);
        BookingRequestDto request = new BookingRequestDto();
        request.setItemId(item.getId());
        request.setStart(start);
        request.setEnd(end);
        bookingService.createBooking(request, booker.getId());

        List<BookingResponseDto> bookings = bookingService.getUserBookings(booker.getId(),
                BookingState.FUTURE, 0, 10);
        assertEquals(1, bookings.size());
    }

    @Test
    void getOwnerBookings_withCurrentState_shouldReturnCurrentBookings() {
        UserDto owner = createOwner();
        UserDto booker = createBooker();
        ItemDto item = createItem(owner);

        LocalDateTime start = LocalDateTime.now().minusHours(1);
        LocalDateTime end = LocalDateTime.now().plusHours(1);
        BookingRequestDto request = new BookingRequestDto();
        request.setItemId(item.getId());
        request.setStart(start);
        request.setEnd(end);
        bookingService.createBooking(request, booker.getId());

        List<BookingResponseDto> bookings = bookingService.getOwnerBookings(owner.getId(),
                BookingState.CURRENT, 0, 10);
        assertEquals(1, bookings.size());
    }

    @Test
    void getOwnerBookings_withPastState_shouldReturnPastBookings() {
        UserDto owner = createOwner();
        UserDto booker = createBooker();
        ItemDto item = createItem(owner);

        LocalDateTime start = LocalDateTime.now().minusDays(2);
        LocalDateTime end = LocalDateTime.now().minusDays(1);
        BookingRequestDto request = new BookingRequestDto();
        request.setItemId(item.getId());
        request.setStart(start);
        request.setEnd(end);
        bookingService.createBooking(request, booker.getId());

        List<BookingResponseDto> bookings = bookingService.getOwnerBookings(owner.getId(),
                BookingState.PAST, 0, 10);
        assertEquals(1, bookings.size());
    }

    @Test
    void getOwnerBookings_withFutureState_shouldReturnFutureBookings() {
        UserDto owner = createOwner();
        UserDto booker = createBooker();
        ItemDto item = createItem(owner);

        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(2);
        BookingRequestDto request = new BookingRequestDto();
        request.setItemId(item.getId());
        request.setStart(start);
        request.setEnd(end);
        bookingService.createBooking(request, booker.getId());

        List<BookingResponseDto> bookings = bookingService.getOwnerBookings(owner.getId(),
                BookingState.FUTURE, 0, 10);
        assertEquals(1, bookings.size());
    }
}