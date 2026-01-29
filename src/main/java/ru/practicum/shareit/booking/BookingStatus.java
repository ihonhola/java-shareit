package ru.practicum.shareit.booking;

public enum BookingStatus {
    ALL,
    WAITING,
    APPROVED,
    REJECTED,
    CURRENT,
    PAST,
    FUTURE;
    //CANCELED

    public static BookingStatus from(String state) {
        if (state == null || state.isBlank()) {
            return ALL;
        }
        try {
            return BookingStatus.valueOf(state.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Unknown state: " + state);
        }
    }
}