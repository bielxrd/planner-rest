package br.com.planner.exceptions;

public class ActivityNotFoundException extends RuntimeException{
    public ActivityNotFoundException(String message) {
        super(message);
    }

}
