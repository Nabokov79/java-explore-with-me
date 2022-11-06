package ru.practicum.ewm.exeption;
public class BadRequestException extends RuntimeException {

    public BadRequestException(String message) {
        super(message);
    }
}
