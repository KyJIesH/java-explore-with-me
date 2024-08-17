package ru.practicum.event.model;

public enum State {
    PENDING,
    PUBLISHED,
    CANCELED,
    REJECTED;

    public static State from(String state) {
        for (State value: State.values()) {
            if (value.name().equalsIgnoreCase(state)) {
                return value;
            }
        }
        return null;
    }
}
