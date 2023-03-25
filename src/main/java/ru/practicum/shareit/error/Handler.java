package ru.practicum.shareit.error;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

@RestControllerAdvice("ru.practicum.shareit")
public class Handler {

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<?> responseStatusHandler(ResponseStatusException e) {
        return ResponseEntity
                .status(e.getStatus())
                .body(e.getMessage());
    }

    @ExceptionHandler(Throwable.class)
    public ResponseEntity<?> throwableStatusHandler(Throwable e) {
        return new ResponseEntity<>(new ErrorResponse("Ошибка"), HttpStatus.valueOf(500));
    }
}
