package ru.practicum.ewm.exeption;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import javax.validation.ConstraintViolationException;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@ControllerAdvice
public class ExceptionsHandler {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private static final String TIMESTAMP = "timestamp";
    private static final String STATUS = "status";
    private static final String ERRORS = "errors";
    private static final String REASONS = "reasons";
    private static final String MESSAGE = "massage";
    public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @ExceptionHandler(value = BadRequestException.class)
    protected ResponseEntity<Object> handleBadRequest(BadRequestException ex) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put(ERRORS, ex.getStackTrace());
        body.put(MESSAGE, ex.getMessage());
        body.put(REASONS, "For the requested operation the conditions are not met.");
        body.put(STATUS, HttpStatus.BAD_REQUEST.getReasonPhrase());
        body.put(TIMESTAMP, OffsetDateTime.now().format(DATE_TIME_FORMATTER));
        logger.error("Bad request error: {}", ex.getMessage(), ex);
        return ResponseEntity.badRequest().body(body);
    }

    @ExceptionHandler(value = NotFoundException.class)
    protected ResponseEntity<Object> handleNotFound(NotFoundException ex) {
        Map<String, Object> body = getGeneralBody(HttpStatus.CONFLICT.getReasonPhrase(),
                                            "The required object was not found.",
                                                   ex.getMessage());
        logger.error("Not found error: {}", ex.getMessage(), ex);
        return new ResponseEntity<>(body,HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {
        Map<String, Object> body = new LinkedHashMap<>();
        List<String> errors = ex.getBindingResult()
                .getAllErrors()
                .stream()
                .map(this::getErrorString).collect(Collectors.toList());
        body.put(ERRORS, errors);
        body.put(MESSAGE, ex.getMessage());
        body.put(REASONS, "Integrity constraint has been violated.");
        body.put(STATUS, HttpStatus.BAD_REQUEST.getReasonPhrase());
        body.put(TIMESTAMP, OffsetDateTime.now().format(DATE_TIME_FORMATTER));
        logger.error("Not valid argument: {}", ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);

    }

    @ExceptionHandler(value = ConstraintViolationException.class)
    protected ResponseEntity<Object> constraintViolationException(ConstraintViolationException ex) {
        Map<String, Object> body = getGeneralBody(HttpStatus.FORBIDDEN.getReasonPhrase(),
                                            "Integrity constraint has been violated.",
                                                   ex.getMessage());
        logger.error("Not valid error: {}", ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(body);
    }

    @ExceptionHandler(value = Throwable.class)
    protected ResponseEntity<Object> handleInternalServerError(Throwable ex) {
        Map<String, Object> body = getGeneralBody(HttpStatus.CONFLICT.getReasonPhrase(),
                                           "Error occurred.", ex.getMessage());
        logger.error("Internal server error: {}", ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.CONFLICT).body(body);
    }

    @ExceptionHandler(value = DateTimeParseException.class)
    protected ResponseEntity<Object> handleDataTime(DateTimeParseException ex) {
        Map<String, Object> body = getGeneralBody(HttpStatus.CONFLICT.getReasonPhrase(),
                                            "Error occurred.",
                                                  ex.getMessage());
        logger.error("DateTime error: {}", ex.getMessage(), ex);
        return ResponseEntity.badRequest().body(body);
    }

    private Map<String, Object> getGeneralBody(String status, String reason, String message) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put(STATUS, status);
        body.put(REASONS, reason);
        body.put(MESSAGE, message);
        body.put(TIMESTAMP, OffsetDateTime.now().format(DATE_TIME_FORMATTER));
        return body;
    }

    private String getErrorString(ObjectError error) {
        if (error instanceof FieldError) {
            return ((FieldError) error).getField() + ' ' + error.getDefaultMessage();
        }
        return error.getDefaultMessage();
    }
}