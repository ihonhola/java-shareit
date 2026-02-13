package ru.practicum.shareit.booking.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

/**
 * TODO Sprint add-bookings.
 */
@Entity
@Table(name = "bookings")
@Data
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "start_date", nullable = false)
    private LocalDateTime start;

    @Column(name = "end_date", nullable = false)
    private LocalDateTime end;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id", nullable = false)
    private Item item;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booker_id", nullable = false)
    private User booker; //пользователь, который осуществляет бронирование

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 24)
    private BookingStatus status;

    public boolean isApproved() {
        return this.status == BookingStatus.APPROVED;
    }

    public boolean isRejected() {
        return this.status == BookingStatus.REJECTED;
    }

    public boolean isWaiting() {
        return this.status == BookingStatus.WAITING;
    }

    public boolean isOwner(Integer userId) {
        return this.item.getOwner().getId().equals(userId);
    }

    public boolean isBooker(Integer userId) {
        return this.booker.getId().equals(userId);
    }

    public boolean isCurrent() {
        LocalDateTime now = LocalDateTime.now();
        return !start.isAfter(now) && !end.isBefore(now);
    }

    public boolean isPast() {
        return end.isBefore(LocalDateTime.now());
    }

    public boolean isFuture() {
        return start.isAfter(LocalDateTime.now());
    }
}
