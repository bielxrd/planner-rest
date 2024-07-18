package br.com.planner.exceptions;

public class TripAlreadyConfirmedException extends RuntimeException {
    public TripAlreadyConfirmedException(String message) {
        super(message);
    }
}
