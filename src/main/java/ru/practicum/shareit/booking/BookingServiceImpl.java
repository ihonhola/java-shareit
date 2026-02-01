package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.exceptions.AccessDeniedException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    @Transactional
    public BookingResponseDto createBooking(BookingRequestDto bookingRequestDto, Integer userId) {
        // Проверяем существование пользователя
        User booker = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));

        // Проверяем существование вещи
        Item item = itemRepository.findById(bookingRequestDto.getItemId())
                .orElseThrow(() -> new NotFoundException("Вещь не найдена"));

        // Проверяем, что пользователь не владелец вещи
        if (itemRepository.existsByIdAndOwnerId(bookingRequestDto.getItemId(), userId)) {
            throw new ValidationException("Владелец не может бронировать свою вещь");
        }

        // Проверяем доступность вещи
        if (!item.getAvailable()) {
            throw new ValidationException("Вещь недоступна для бронирования");
        }

        // Проверяем даты бронирования
        LocalDateTime start = bookingRequestDto.getStart();
        LocalDateTime end = bookingRequestDto.getEnd();

        //аннотации не проверяют, что end должен быть после start (а не просто в будущем)
        if (end.isBefore(start) || end.equals(start)) {
            throw new ValidationException("Дата окончания должна быть после даты начала");
        }

        // Проверяем, что вещь свободна на указанные даты
        if (!bookingRepository.isItemAvailable(item.getId(), start, end)) {
            throw new ValidationException("Вещь уже забронирована на указанные даты");
        }

        // Создаем бронирование
        Booking booking = BookingMapper.toBooking(bookingRequestDto, item, booker);
        Booking savedBooking = bookingRepository.save(booking);

        log.info("Создано бронирование ID: {} пользователем ID: {} для вещи ID: {}",
                savedBooking.getId(), userId, item.getId());

        return BookingMapper.toBookingResponseDto(savedBooking);
    }

    @Override
    @Transactional
    public BookingResponseDto approveBooking(Integer bookingId, Integer userId, boolean approved) {
        // Проверяем существование пользователя
        if (!userRepository.existsById(userId)) {
            throw new ValidationException("Пользователь не найден");
        }

        // Находим бронирование
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Бронирование не найдено"));

        // Проверяем, что пользователь - владелец вещи
        if (!booking.isOwner(userId)) {
            throw new AccessDeniedException("Только владелец вещи может подтвердить или отклонить бронирование");
        }

        // Проверяем статус бронирования
        if (!booking.isWaiting()) {
            throw new ValidationException("Бронирование уже было обработано");
        }

        // Устанавливаем новый статус
        booking.setStatus(approved ? BookingStatus.APPROVED : BookingStatus.REJECTED);
        Booking updatedBooking = bookingRepository.save(booking);

        log.info("Бронирование ID: {} {} пользователем ID: {}",
                bookingId, approved ? "подтверждено" : "отклонено", userId);

        return BookingMapper.toBookingResponseDto(updatedBooking);
    }

    @Override
    public BookingResponseDto getBookingById(Integer bookingId, Integer userId) {
        // Проверяем существование пользователя
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("Пользователь не найден");
        }

        // Находим бронирование
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Бронирование не найдено"));

        // Проверяем права доступа
        if (!booking.isOwner(userId) && !booking.isBooker(userId)) {
            throw new AccessDeniedException("Доступ к бронированию запрещен");
        }

        return BookingMapper.toBookingResponseDto(booking);
    }

    @Override
    public List<BookingResponseDto> getUserBookings(Integer userId, BookingState state, int from, int size) {
        // Проверяем существование пользователя
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("Пользователь не найден");
        }

        // Настраиваем пагинацию
        Pageable pageable = PageRequest.of(from / size, size, Sort.by(Sort.Direction.DESC, "start"));
        List<Booking> bookings;
        LocalDateTime now = LocalDateTime.now();

        // Получаем бронирования в зависимости от состояния
        switch (state) {
            case CURRENT:
                bookings = bookingRepository.findAllByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(
                        userId, now, now, pageable);
                break;
            case PAST:
                bookings = bookingRepository.findAllByBookerIdAndEndBeforeOrderByStartDesc(
                        userId, now, pageable);
                break;
            case FUTURE:
                bookings = bookingRepository.findAllByBookerIdAndStartAfterOrderByStartDesc(
                        userId, now, pageable);
                break;
            case WAITING:
                bookings = bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(
                        userId, BookingStatus.WAITING, pageable);
                break;
            case REJECTED:
                bookings = bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(
                        userId, BookingStatus.REJECTED, pageable);
                break;
            default: // ALL
                bookings = bookingRepository.findAllByBookerIdOrderByStartDesc(userId, pageable);
        }

        return bookings.stream()
                .map(BookingMapper::toBookingResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<BookingResponseDto> getOwnerBookings(Integer ownerId, BookingState state, int from, int size) {
        // Проверяем существование пользователя
        if (!userRepository.existsById(ownerId)) {
            throw new NotFoundException("Пользователь не найден");
        }

        // Проверяем, что у пользователя есть вещи
        if (!itemRepository.existsByOwnerId(ownerId)) {
            throw new ValidationException("У пользователя нет вещей для управления бронированиями");
        }

        // Настраиваем пагинацию
        Pageable pageable = PageRequest.of(from / size, size, Sort.by(Sort.Direction.DESC, "start"));
        List<Booking> bookings;
        LocalDateTime now = LocalDateTime.now();

        // Получаем бронирования в зависимости от состояния
        switch (state) {
            case CURRENT:
                bookings = bookingRepository.findCurrentByOwnerId(ownerId, now, pageable);
                break;
            case PAST:
                bookings = bookingRepository.findAllByItemOwnerIdAndEndBeforeOrderByStartDesc(
                        ownerId, now, pageable);
                break;
            case FUTURE:
                bookings = bookingRepository.findAllByItemOwnerIdAndStartAfterOrderByStartDesc(
                        ownerId, now, pageable);
                break;
            case WAITING:
                bookings = bookingRepository.findAllByItemOwnerIdAndStatusOrderByStartDesc(
                        ownerId, BookingStatus.WAITING, pageable);
                break;
            case REJECTED:
                bookings = bookingRepository.findAllByItemOwnerIdAndStatusOrderByStartDesc(
                        ownerId, BookingStatus.REJECTED, pageable);
                break;
            default: // ALL
                bookings = bookingRepository.findAllByItemOwnerIdOrderByStartDesc(ownerId, pageable);
        }

        return bookings.stream()
                .map(BookingMapper::toBookingResponseDto)
                .collect(Collectors.toList());
    }
}