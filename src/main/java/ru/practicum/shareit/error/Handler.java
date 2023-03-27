package ru.practicum.shareit.error;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

@Slf4j
@RestControllerAdvice("ru.practicum.shareit")
public class Handler {

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<?> responseStatusHandler(ResponseStatusException e) {
        log.error("произошла ошибка " + e.getMessage());
        return ResponseEntity
                .status(e.getStatus())
                .body(e.getMessage());
    }

    @ExceptionHandler(Throwable.class)
    public ResponseEntity<?> throwableStatusHandler(Throwable e) {
        log.error("произошла ошибка " + e.getMessage());
        return new ResponseEntity<>(new ErrorResponse("Ошибка"), HttpStatus.valueOf(500));
    }
}
