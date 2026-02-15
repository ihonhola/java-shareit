package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingService;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.dto.CommentRequestDto;
import ru.practicum.shareit.item.dto.CommentResponseDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.booking.model.Booking;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
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

    @Autowired
    private BookingService bookingService;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private ItemRequestRepository requestRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Test
    void createItem_shouldSaveAndReturnItem() {
        UserDto owner = userService.createUser(new UserDto(null, "Owner", "owner@mail.com"));
        ItemDto itemDto = new ItemDto(null, "Гиперболоид", "Принадлежал инженеру Гарину",
                true, null, null, null, null);
        ItemDto saved = itemService.createItem(itemDto, owner.getId());

        assertNotNull(saved.getId());
        assertEquals("Гиперболоид", saved.getName());
        assertTrue(saved.getAvailable());
    }

    @Test
    void createItem_withRequestId_shouldLinkRequest() {
        UserDto requestor = userService.createUser(new UserDto(null, "Requestor", "req@mail.com"));
        UserDto owner = userService.createUser(new UserDto(null, "Owner", "owner@mail.com"));

        RequestDto requestDto = new RequestDto();
        requestDto.setDescription("Нужен гиперболоид");
        ItemRequest request = new ItemRequest();
        request.setDescription(requestDto.getDescription());
        request.setRequestor(userRepository.findById(requestor.getId()).orElseThrow());
        request.setCreated(LocalDateTime.now());
        request = requestRepository.save(request);

        ItemDto itemDto = new ItemDto(null, "Гиперболоид", "Принадлежал инженеру Гарину",
                true, request.getId(), null, null, null);
        ItemDto saved = itemService.createItem(itemDto, owner.getId());

        assertEquals(request.getId(), saved.getRequestId());
    }

    @Test
    void createItem_ownerNotFound_shouldThrowNotFound() {
        ItemDto itemDto = new ItemDto(null, "Гиперболоид", "Принадлежал инженеру Гарину",
                true, null, null, null, null);
        assertThrows(NotFoundException.class, () -> itemService.createItem(itemDto, 999));
    }

    @Test
    void createItem_requestNotFound_shouldThrowNotFound() {
        UserDto owner = userService.createUser(new UserDto(null, "Owner", "owner@mail.com"));
        ItemDto itemDto = new ItemDto(null, "Гиперболоид", "Принадлежал инженеру Гарину",
                true, 999, null, null, null);
        assertThrows(NotFoundException.class, () -> itemService.createItem(itemDto, owner.getId()));
    }

    @Test
    void createItem_ownerEqualsRequestor_shouldThrowValidation() {
        UserDto owner = userService.createUser(new UserDto(null, "Owner", "owner@mail.com"));
        RequestDto requestDto = new RequestDto();
        requestDto.setDescription("Нужен гиперболоид");
        ItemRequest request = new ItemRequest();
        request.setDescription(requestDto.getDescription());
        request.setRequestor(userRepository.findById(owner.getId()).orElseThrow());
        request.setCreated(LocalDateTime.now());
        request = requestRepository.save(request);

        ItemDto itemDto = new ItemDto(null, "Гиперболоид", "Принадлежал инженеру Гарину",
                true, request.getId(), null, null, null);
        assertThrows(ValidationException.class, () -> itemService.createItem(itemDto, owner.getId()));
    }

    @Test
    void updateItem_shouldUpdateFields() {
        UserDto owner = userService.createUser(new UserDto(null, "Owner", "owner@mail.com"));
        ItemDto created = itemService.createItem(
                new ItemDto(null, "Гиперболоид", "Принадлежал инженеру Гарину",
                        true, null, null, null, null),
                owner.getId()
        );

        ItemDto update = new ItemDto(null, "Молот", "Принадлежал инженеру Тору",
                false, null, null, null, null);
        ItemDto updated = itemService.updateItem(created.getId(), update, owner.getId());

        assertEquals("Молот", updated.getName());
        assertEquals("Принадлежал инженеру Тору", updated.getDescription());
        assertFalse(updated.getAvailable());
    }

    @Test
    void updateItem_partialUpdate_shouldUpdateOnlyProvided() {
        UserDto owner = userService.createUser(new UserDto(null, "Owner", "owner@mail.com"));
        ItemDto created = itemService.createItem(
                new ItemDto(null, "Гиперболоид", "Принадлежал инженеру Гарину",
                        true, null, null, null, null),
                owner.getId()
        );

        ItemDto update = new ItemDto(null, "Молот", null, null,
                null, null, null, null);
        ItemDto updated = itemService.updateItem(created.getId(), update, owner.getId());

        assertEquals("Молот", updated.getName());
        assertEquals("Принадлежал инженеру Гарину", updated.getDescription());
        assertTrue(updated.getAvailable());
    }

    @Test
    void updateItem_notFound_shouldThrowNotFound() {
        UserDto owner = userService.createUser(new UserDto(null, "Owner", "owner@mail.com"));
        ItemDto update = new ItemDto(null, "Молот", null, null, null,
                null, null, null);
        assertThrows(NotFoundException.class, () -> itemService.updateItem(999, update, owner.getId()));
    }

    @Test
    void updateItem_notOwner_shouldThrowNotFound() {
        UserDto owner = userService.createUser(new UserDto(null, "Owner", "owner@mail.com"));
        UserDto other = userService.createUser(new UserDto(null, "Other", "other@mail.com"));
        ItemDto created = itemService.createItem(
                new ItemDto(null, "Гиперболоид", "Принадлежал инженеру Гарину",
                        true, null, null, null, null),
                owner.getId()
        );

        ItemDto update = new ItemDto(null, "Молот", null, null, null,
                null, null, null);
        assertThrows(NotFoundException.class, () -> itemService.updateItem(created.getId(), update, other.getId()));
    }

    @Test
    void getItemById_asNonOwner_shouldNotIncludeBookings() {
        UserDto owner = userService.createUser(new UserDto(null, "Owner", "owner@mail.com"));
        UserDto other = userService.createUser(new UserDto(null, "Other", "other@mail.com"));
        ItemDto created = itemService.createItem(
                new ItemDto(null, "Гиперболоид", "Принадлежал инженеру Гарину",
                        true, null, null, null, null),
                owner.getId()
        );

        ItemDto found = itemService.getItemById(created.getId(), other.getId());
        assertNull(found.getLastBooking());
        assertNull(found.getNextBooking());
    }

    @Test
    void getItemById_notFound_shouldThrowNotFound() {
        assertThrows(NotFoundException.class, () -> itemService.getItemById(999, 1));
    }

    @Test
    void getAllItemsByOwner_shouldReturnListWithBookings() {
        UserDto owner = userService.createUser(new UserDto(null, "Owner", "owner@mail.com"));
        ItemDto item1 = itemService.createItem(
                new ItemDto(null, "Гиперболоид", "Принадлежал инженеру Гарину",
                        true, null,
                        null, null, null),
                owner.getId()
        );
        ItemDto item2 = itemService.createItem(
                new ItemDto(null, "Молот", "Принадлежал инженеру Тору", true,
                        null, null, null, null),
                owner.getId()
        );

        List<ItemDto> items = itemService.getAllItemsByOwner(owner.getId());
        assertEquals(2, items.size());
    }

    @Test
    void getAllItemsByOwner_userNotFound_shouldThrowNotFound() {
        assertThrows(NotFoundException.class, () -> itemService.getAllItemsByOwner(999));
    }

    @Test
    void searchAvailableItems_shouldReturnMatching() {
        UserDto owner = userService.createUser(new UserDto(null, "Owner", "owner@mail.com"));
        itemService.createItem(
                new ItemDto(null, "Гиперболоид", "Принадлежал инженеру Гарину",
                        true, null, null, null, null),
                owner.getId()
        );
        itemService.createItem(
                new ItemDto(null, "Молот", "Принадлежал инженеру Тору", true,
                        null, null, null, null),
                owner.getId()
        );

        List<ItemDto> result = itemService.searchAvailableItems("инженер");
        assertEquals(2, result.size());

        result = itemService.searchAvailableItems("гиперболоид");
        assertEquals(1, result.size());
    }

    @Test
    void searchAvailableItems_blankText_shouldReturnEmpty() {
        List<ItemDto> result = itemService.searchAvailableItems("");
        assertTrue(result.isEmpty());
    }

    @Test
    void addComment_shouldSaveComment() {
        UserDto owner = userService.createUser(new UserDto(null, "Owner", "owner@mail.com"));
        UserDto booker = userService.createUser(new UserDto(null, "Booker", "booker@mail.com"));
        ItemDto item = itemService.createItem(
                new ItemDto(null, "Гиперболоид", "Принадлежал инженеру Гарину", true,
                        null, null, null, null),
                owner.getId()
        );

        // Создаём завершённое бронирование
        Booking booking = new Booking();
        booking.setStart(LocalDateTime.now().minusDays(2));
        booking.setEnd(LocalDateTime.now().minusDays(1));
        booking.setItem(itemRepository.findById(item.getId()).orElseThrow());
        booking.setBooker(userRepository.findById(booker.getId()).orElseThrow());
        booking.setStatus(BookingStatus.APPROVED);
        bookingRepository.save(booking);

        CommentRequestDto commentRequest = new CommentRequestDto();
        commentRequest.setText("Great!");

        CommentResponseDto comment = itemService.addComment(item.getId(), commentRequest, booker.getId());

        assertNotNull(comment.getId());
        assertEquals("Great!", comment.getText());
        assertEquals(booker.getName(), comment.getAuthorName());
        assertNotNull(comment.getCreated());
    }

    @Test
    void addComment_itemNotFound_shouldThrowNotFound() {
        UserDto booker = userService.createUser(new UserDto(null, "Booker", "booker@mail.com"));
        CommentRequestDto commentRequest = new CommentRequestDto();
        commentRequest.setText("Great!");
        assertThrows(NotFoundException.class, () ->
                itemService.addComment(999, commentRequest, booker.getId()));
    }

    @Test
    void addComment_userNotFound_shouldThrowNotFound() {
        UserDto owner = userService.createUser(new UserDto(null, "Owner", "owner@mail.com"));
        ItemDto item = itemService.createItem(
                new ItemDto(null, "Гиперболоид", "Принадлежал инженеру Гарину",
                        true, null, null, null, null),
                owner.getId()
        );
        CommentRequestDto commentRequest = new CommentRequestDto();
        commentRequest.setText("Great!");
        assertThrows(NotFoundException.class, () ->
                itemService.addComment(item.getId(), commentRequest, 999));
    }

    @Test
    void addComment_userNeverBooked_shouldThrowValidation() {
        UserDto owner = userService.createUser(new UserDto(null, "Owner", "owner@mail.com"));
        UserDto other = userService.createUser(new UserDto(null, "Other", "other@mail.com"));
        ItemDto item = itemService.createItem(
                new ItemDto(null, "Гиперболоид", "Принадлежал инженеру Гарину",
                        true, null, null, null, null),
                owner.getId()
        );

        CommentRequestDto commentRequest = new CommentRequestDto();
        commentRequest.setText("Great!");
        assertThrows(ValidationException.class, () ->
                itemService.addComment(item.getId(), commentRequest, other.getId()));
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

    @Test
    void getAllItemsByOwner_withMultipleBookings_shouldSetLastAndNext() {
        // Создаём владельца
        UserDto owner = userService.createUser(new UserDto(null, "Owner", "owner@mail.com"));

        // Создаём двух букеров
        UserDto booker1 = userService.createUser(new UserDto(null, "Booker1", "booker1@mail.com"));
        UserDto booker2 = userService.createUser(new UserDto(null, "Booker2", "booker2@mail.com"));

        // Создаём вещь
        ItemDto item = itemService.createItem(
                new ItemDto(null, "Гиперболоид", "Принадлежал инженеру Гарину",
                        true, null, null, null, null),
                owner.getId()
        );

        // Создаём прошлое бронирование (APPROVED)
        LocalDateTime pastStart = LocalDateTime.now().minusDays(5);
        LocalDateTime pastEnd = LocalDateTime.now().minusDays(4);
        BookingRequestDto pastRequest = new BookingRequestDto();
        pastRequest.setItemId(item.getId());
        pastRequest.setStart(pastStart);
        pastRequest.setEnd(pastEnd);
        BookingResponseDto pastBooking = bookingService.createBooking(pastRequest, booker1.getId());
        bookingService.approveBooking(pastBooking.getId(), owner.getId(), true);

        // Создаём будущее бронирование (APPROVED)
        LocalDateTime futureStart = LocalDateTime.now().plusDays(1);
        LocalDateTime futureEnd = LocalDateTime.now().plusDays(2);
        BookingRequestDto futureRequest = new BookingRequestDto();
        futureRequest.setItemId(item.getId());
        futureRequest.setStart(futureStart);
        futureRequest.setEnd(futureEnd);
        BookingResponseDto futureBooking = bookingService.createBooking(futureRequest, booker2.getId());
        bookingService.approveBooking(futureBooking.getId(), owner.getId(), true);

        // Получаем все вещи владельца
        List<ItemDto> items = itemService.getAllItemsByOwner(owner.getId());
        assertEquals(1, items.size());
        ItemDto result = items.get(0);

        // Проверяем last и next бронирования
        assertNotNull(result.getLastBooking());
        assertNotNull(result.getNextBooking());
        assertEquals(pastBooking.getId(), result.getLastBooking().getId());
        assertEquals(futureBooking.getId(), result.getNextBooking().getId());
    }

    @Test
    void getItemById_asOwner_withMultipleBookings_shouldSetLastAndNext() {
        // Создаём владельца
        UserDto owner = userService.createUser(new UserDto(null, "Owner", "owner@mail.com"));

        // Создаём букеров
        UserDto booker1 = userService.createUser(new UserDto(null, "Booker1", "booker1@mail.com"));
        UserDto booker2 = userService.createUser(new UserDto(null, "Booker2", "booker2@mail.com"));

        // Создаём вещь
        ItemDto item = itemService.createItem(
                new ItemDto(null, "Гиперболоид", "Принадлежал инеженру Гарину",
                        true, null, null, null, null),
                owner.getId()
        );

        // Прошлое бронирование
        LocalDateTime pastStart = LocalDateTime.now().minusDays(5);
        LocalDateTime pastEnd = LocalDateTime.now().minusDays(4);
        BookingRequestDto pastRequest = new BookingRequestDto();
        pastRequest.setItemId(item.getId());
        pastRequest.setStart(pastStart);
        pastRequest.setEnd(pastEnd);
        BookingResponseDto pastBooking = bookingService.createBooking(pastRequest, booker1.getId());
        bookingService.approveBooking(pastBooking.getId(), owner.getId(), true);

        // Будущее бронирование
        LocalDateTime futureStart = LocalDateTime.now().plusDays(1);
        LocalDateTime futureEnd = LocalDateTime.now().plusDays(2);
        BookingRequestDto futureRequest = new BookingRequestDto();
        futureRequest.setItemId(item.getId());
        futureRequest.setStart(futureStart);
        futureRequest.setEnd(futureEnd);
        BookingResponseDto futureBooking = bookingService.createBooking(futureRequest, booker2.getId());
        bookingService.approveBooking(futureBooking.getId(), owner.getId(), true);

        // Получаем вещь по ID от имени владельца
        ItemDto result = itemService.getItemById(item.getId(), owner.getId());

        assertNotNull(result.getLastBooking());
        assertNotNull(result.getNextBooking());
        assertEquals(pastBooking.getId(), result.getLastBooking().getId());
        assertEquals(futureBooking.getId(), result.getNextBooking().getId());
    }
}