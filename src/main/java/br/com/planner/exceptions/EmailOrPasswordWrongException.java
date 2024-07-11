package br.com.planner.exceptions;

public class EmailOrPasswordWrongException extends RuntimeException {
        public EmailOrPasswordWrongException(String message) {
            super(message);
        }
}
