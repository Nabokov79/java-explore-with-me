package ru.practicum.ewm.events.model;


import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public enum State {

    PENDING,
    PUBLISHED,
    CANCELED;

    public static Optional<State> from(String stringState) {
        for (State state : values()) {
            if (state.name().equalsIgnoreCase(stringState)) {
                return Optional.of(state);
            }
        }
        return Optional.empty();
    }
}
