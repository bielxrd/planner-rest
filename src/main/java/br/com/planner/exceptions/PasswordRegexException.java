package br.com.planner.exceptions;

public class PasswordRegexException extends RuntimeException {

    public PasswordRegexException(String message) {
        super(message);
    }
}
