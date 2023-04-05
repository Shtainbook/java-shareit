package ru.practicum.shareit.error.handler.exception;

import lombok.Getter;

@Getter
public class StateException extends RuntimeException {
    private final String message;

    public StateException(String message) {
        this.message = message;
    }
}
