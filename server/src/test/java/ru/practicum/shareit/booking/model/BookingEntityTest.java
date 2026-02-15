package ru.practicum.shareit.booking.model;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.booking.BookingStatus;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BookingEntityTest {

    @Test
    void isOwner_shouldReturnTrue_whenUserIsOwner() {
        User owner = new User();
        owner.setId(1);
        Item item = new Item();
        item.setOwner(owner);
        Booking booking = new Booking();
        booking.setItem(item);
        assertTrue(booking.isOwner(1));
        assertFalse(booking.isOwner(2));
    }

    @Test
    void isBooker_shouldReturnTrue_whenUserIsBooker() {
        User booker = new User();
        booker.setId(1);
        Booking booking = new Booking();
        booking.setBooker(booker);
        assertTrue(booking.isBooker(1));
        assertFalse(booking.isBooker(2));
    }

    @Test
    void isApproved_shouldReturnTrue_whenStatusApproved() {
        Booking booking = new Booking();
        booking.setStatus(BookingStatus.APPROVED);
        assertTrue(booking.isApproved());
        booking.setStatus(BookingStatus.WAITING);
        assertFalse(booking.isApproved());
    }

    @Test
    void isRejected_shouldReturnTrue_whenStatusRejected() {
        Booking booking = new Booking();
        booking.setStatus(BookingStatus.REJECTED);
        assertTrue(booking.isRejected());
        booking.setStatus(BookingStatus.WAITING);
        assertFalse(booking.isRejected());
    }

    @Test
    void isWaiting_shouldReturnTrue_whenStatusWaiting() {
        Booking booking = new Booking();
        booking.setStatus(BookingStatus.WAITING);
        assertTrue(booking.isWaiting());
        booking.setStatus(BookingStatus.APPROVED);
        assertFalse(booking.isWaiting());
    }

    @Test
    void isCurrent_shouldReturnTrue_whenNowBetweenStartAndEnd() {
        Booking booking = new Booking();
        LocalDateTime now = LocalDateTime.now();
        booking.setStart(now.minusHours(1));
        booking.setEnd(now.plusHours(1));
        assertTrue(booking.isCurrent());

        booking.setStart(now.plusHours(1));
        booking.setEnd(now.plusHours(2));
        assertFalse(booking.isCurrent());

        booking.setStart(now.minusHours(2));
        booking.setEnd(now.minusHours(1));
        assertFalse(booking.isCurrent());
    }

    @Test
    void isPast_shouldReturnTrue_whenEndBeforeNow() {
        Booking booking = new Booking();
        LocalDateTime now = LocalDateTime.now();
        booking.setEnd(now.minusMinutes(1));
        assertTrue(booking.isPast());
        booking.setEnd(now.plusMinutes(1));
        assertFalse(booking.isPast());
    }

    @Test
    void isFuture_shouldReturnTrue_whenStartAfterNow() {
        Booking booking = new Booking();
        LocalDateTime now = LocalDateTime.now();
        booking.setStart(now.plusMinutes(1));
        assertTrue(booking.isFuture());
        booking.setStart(now.minusMinutes(1));
        assertFalse(booking.isFuture());
    }
}