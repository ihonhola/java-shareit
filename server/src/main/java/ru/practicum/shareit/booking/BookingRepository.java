package ru.practicum.shareit.booking;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.model.Booking;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Integer> {

    // Основные методы поиска по пользователю
    List<Booking> findAllByBookerIdOrderByStartDesc(Integer bookerId, Pageable pageable);

    List<Booking> findAllByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(
            Integer bookerId, LocalDateTime start, LocalDateTime end, Pageable pageable);

    List<Booking> findAllByBookerIdAndEndBeforeOrderByStartDesc(
            Integer bookerId, LocalDateTime end, Pageable pageable);

    List<Booking> findAllByBookerIdAndStartAfterOrderByStartDesc(
            Integer bookerId, LocalDateTime start, Pageable pageable);

    List<Booking> findAllByBookerIdAndStatusOrderByStartDesc(
            Integer bookerId, BookingStatus status, Pageable pageable);

    // Методы для владельца вещей
    List<Booking> findAllByItemOwnerIdOrderByStartDesc(Integer ownerId, Pageable pageable);

    @Query("SELECT b FROM Booking b " +
            "WHERE b.item.owner.id = ?1 " +
            "AND b.start <= ?2 " +
            "AND b.end >= ?2 " +
            "ORDER BY b.start DESC")
    List<Booking> findCurrentByOwnerId(
            Integer ownerId, LocalDateTime now, Pageable pageable);

    List<Booking> findAllByItemOwnerIdAndEndBeforeOrderByStartDesc(
            Integer ownerId, LocalDateTime end, Pageable pageable);

    List<Booking> findAllByItemOwnerIdAndStartAfterOrderByStartDesc(
            Integer ownerId, LocalDateTime start, Pageable pageable);

    List<Booking> findAllByItemOwnerIdAndStatusOrderByStartDesc(
            Integer ownerId, BookingStatus status, Pageable pageable);

    // Проверка доступности вещи
    @Query("SELECT CASE WHEN COUNT(b) > 0 THEN false ELSE true END " +
            "FROM Booking b " +
            "WHERE b.item.id = ?1 " +
            "AND b.status IN ('APPROVED', 'WAITING') " +
            "AND NOT (b.end <= ?2 OR b.start >= ?3)")
    boolean isItemAvailable(
            Integer itemId, LocalDateTime start, LocalDateTime end);

    @Query("SELECT b FROM Booking b " +
            "WHERE b.item.id = ?1 " +
            "AND (?2 IS NULL OR b.booker.id = ?2) " +
            "AND b.end < ?3 " +
            "AND b.status = ?4 " +
            "ORDER BY b.end DESC")
    Optional<Booking> findFirstByItemIdAndBookerIdAndEndBeforeAndStatus(
            Integer itemId, Integer bookerId, LocalDateTime currentTime, BookingStatus status);

    // Метод для поиска следующего бронирования
    @Query("SELECT b FROM Booking b " +
            "WHERE b.item.id = ?1 " +
            "AND b.status != ?2 " +
            "AND b.start > ?3 " +
            "ORDER BY b.start ASC")
    List<Booking> findAllByItemIdAndStatusAndStartAfterOrderByStartAsc(
            Integer itemId, BookingStatus status, LocalDateTime currentTime);

    @Query("SELECT COUNT(b) > 0 FROM Booking b " +
            "WHERE b.item.id = ?1 " +
            "AND b.booker.id = ?2 " +
            "AND b.end < ?3 " +
            "AND b.status != 'REJECTED'")
    boolean existsByItemIdAndBookerIdAndEndBeforeAndStatusNotRejected(
            Integer itemId, Integer bookerId, LocalDateTime currentTime);

    // Получаем все завершенные бронирования, отсортированные по дате окончания
    @Query("SELECT b FROM Booking b " +
            "WHERE b.item.id IN ?1 " +
            "AND b.status = ?2 " +
            "AND b.end < ?3 " +
            "ORDER BY b.item.id, b.end DESC")
    List<Booking> findAllCompletedBookingsForItems(
            List<Integer> itemIds, BookingStatus status, LocalDateTime currentTime);

    // Получаем все будущие бронирования, отсортированные по дате начала
    @Query("SELECT b FROM Booking b " +
            "WHERE b.item.id IN ?1 " +
            "AND b.status = ?2 " +
            "AND b.start > ?3 " +
            "ORDER BY b.item.id, b.start ASC")
    List<Booking> findAllFutureBookingsForItems(
            List<Integer> itemIds, BookingStatus status, LocalDateTime currentTime);
}