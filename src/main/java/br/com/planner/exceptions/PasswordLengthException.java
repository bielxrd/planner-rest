package br.com.planner.exceptions;

public class PasswordLengthException extends RuntimeException {

        public PasswordLengthException(String message) {
            super(message);
        }
}
