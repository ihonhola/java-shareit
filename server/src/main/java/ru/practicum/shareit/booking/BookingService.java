package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;

import java.util.List;

public interface BookingService {

    BookingResponseDto createBooking(BookingRequestDto bookingRequestDto, Integer userId);

    BookingResponseDto approveBooking(Integer bookingId, Integer userId, boolean approved);

    BookingResponseDto getBookingById(Integer bookingId, Integer userId);

    List<BookingResponseDto> getUserBookings(Integer userId, BookingState state, int from, int size);

    List<BookingResponseDto> getOwnerBookings(Integer ownerId, BookingState state, int from, int size);
}