package ru.practicum.shareit.booking;

import java.util.Arrays;

public enum BookingState {
    ALL,
    CURRENT,
    PAST,
    FUTURE,
    WAITING,
    REJECTED;

    public static BookingState from(String state) {
        if (state == null || state.isBlank()) {
            return ALL;
        }

        String upperState = state.toUpperCase();
        return Arrays.stream(BookingState.values())
                .filter(status -> status.name().equals(upperState))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown state: " + state));
    }
}