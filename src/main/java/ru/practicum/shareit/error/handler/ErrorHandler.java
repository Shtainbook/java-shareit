package ru.practicum.shareit.error.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.shareit.error.handler.exception.StateException;
import ru.practicum.shareit.error.handler.responce.StateErrorResponse;

import java.util.Objects;

@Slf4j
@RestControllerAdvice("ru.practicum.shareit")
public class ErrorHandler {

    @ExceptionHandler(ResponseStatusException.class)
    private ResponseEntity<String> handleException(ResponseStatusException e) {
        log.error("произошла ошибка " + e.getMessage());
        return ResponseEntity
                .status(e.getStatus())
                .body(e.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    private ResponseEntity<String> handleException(MethodArgumentNotValidException e) {
        log.error("произошла ошибка " + e.getMessage());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(HttpStatus.BAD_REQUEST + " " + Objects.requireNonNull(e.getFieldError()).getDefaultMessage());
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    private ResponseEntity<String> handleException(MethodArgumentTypeMismatchException e) {
        log.error("произошла ошибка " + e.getMessage());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(HttpStatus.BAD_REQUEST + " Некорректные параметры строки " + e.getName() + "=" + e.getValue());
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    private ResponseEntity<String> handleException() {
        log.error("произошла ошибка.");
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(HttpStatus.INTERNAL_SERVER_ERROR + " Нарушение уникального индекса или первичного ключа");
    }

    @ExceptionHandler(StateException.class)
    private ResponseEntity<StateErrorResponse> handleException(StateException e) {
        log.error("произошла ошибка " + e.getMessage());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new StateErrorResponse(e.getMessage()));
    }
}
