package ru.practicum.ewm.exeption;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import javax.validation.ConstraintViolationException;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@ControllerAdvice
public class ExceptionsHandler extends ResponseEntityExceptionHandler {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private static final String TIMESTAMP = "timestamp";
    private static final String STATUS = "status";
    private static final String ERROR = "error";
    private static final String REASONS = "reasons";
    private static final String MESSAGE = "massage";
    public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @ExceptionHandler(value = NotFoundException.class)
    protected ResponseEntity<Object> handleNotFound(NotFoundException ex, WebRequest request) {
        logger.error("Not found error: {}", ex.getMessage(), ex);
        Map<String, Object> body = new LinkedHashMap<>();
        body.put(STATUS, HttpStatus.NOT_FOUND.getReasonPhrase());
        body.put(REASONS, "The required object was not found.");
        body.put(MESSAGE, ex.getMessage());
        body.put(TIMESTAMP, OffsetDateTime.now().format(DATE_TIME_FORMATTER));
        return handleExceptionInternal(ex, body, new HttpHeaders(), HttpStatus.NOT_FOUND, request);
    }

    @ExceptionHandler(value = BadRequestException.class)
    protected ResponseEntity<Object> handleNotFound(BadRequestException ex, WebRequest request) {
        logger.error("Bad request error: {}", ex.getMessage(), ex);
        Map<String, Object> body = new LinkedHashMap<>();
        body.put(ERROR, HttpStatus.BAD_REQUEST.getReasonPhrase());
        body.put(STATUS, HttpStatus.BAD_REQUEST.getReasonPhrase());
        body.put(REASONS, "For the requested operation the conditions are not met.");
        body.put(MESSAGE, ex.getMessage());
        body.put(TIMESTAMP, OffsetDateTime.now().format(DATE_TIME_FORMATTER));
        return handleExceptionInternal(ex, body, new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler(value = ConstraintViolationException.class)
    protected ResponseEntity<Object> constraintViolationException(ConstraintViolationException ex, WebRequest request) {
        logger.error("Constraint error: {}", ex.getMessage(), ex);
        Map<String, Object> body = getGeneralErrorBody(HttpStatus.BAD_REQUEST);
        List<String> errors = Arrays.stream(ex.getMessage().split(", "))
                                    .collect(Collectors.toList());
        body.put(REASONS, errors);
        return handleExceptionInternal(ex, body, new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                                  HttpHeaders headers,
                                                                  HttpStatus status,
                                                                  WebRequest request) {
        logger.error("Not Valid. Message: {}", ex.getMessage(), ex);
        Map<String, Object> body = getGeneralErrorBody(status);
        List<String> errors = ex.getBindingResult()
                .getAllErrors()
                .stream()
                .map(this::getErrorString)
                .collect(Collectors.toList());
        body.put(REASONS, errors);
        return new ResponseEntity<>(body, headers, status);
    }

    private Map<String, Object> getGeneralErrorBody(HttpStatus status) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put(TIMESTAMP, OffsetDateTime.now().format(DATE_TIME_FORMATTER));
        body.put(STATUS, status.value());
        body.put(ERROR, status.getReasonPhrase());
        return body;
    }

    private String getErrorString(ObjectError error) {
        if (error instanceof FieldError) {
            return ((FieldError) error).getField() + ' ' + error.getDefaultMessage();
        }
        return error.getDefaultMessage();
    }
}