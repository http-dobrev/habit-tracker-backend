package com.konstantin.habittracker.exception;

public class InvalidHabitDataException extends RuntimeException {
    public InvalidHabitDataException(String message) {
        super(message);
    }
}