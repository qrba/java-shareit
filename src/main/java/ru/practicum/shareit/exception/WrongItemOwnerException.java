package ru.practicum.shareit.exception;

public class WrongItemOwnerException extends RuntimeException {
    public WrongItemOwnerException(String message) {
        super(message);
    }
}
