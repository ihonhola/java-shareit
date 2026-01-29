package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingDto;

import java.util.List;

public interface BookingService {

    BookingDto createBooking(BookingDto bookingDto, Integer userId);

    BookingDto approveBooking(Integer bookingId, Integer userId, boolean approved);

    BookingDto getBookingById(Integer bookingId, Integer userId);

    List<BookingDto> getUserBookings(Integer userId, BookingStatus state, int from, int size);

    List<BookingDto> getOwnerBookings(Integer ownerId, BookingStatus state, int from, int size);
}