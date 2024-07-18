package br.com.planner.handler;

import br.com.planner.exceptions.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
public class ExceptionHandlerRest extends ResponseEntityExceptionHandler {

    @ExceptionHandler(TokenInvalidException.class)
    private ResponseEntity<Object> handleTokenInvalidException(TokenInvalidException tokenInvalidException) {
        ErrorMessageDTO threatResponse = new ErrorMessageDTO(HttpStatus.UNAUTHORIZED, tokenInvalidException.getMessage());
        return ResponseEntity.status(threatResponse.getStatus()).body(threatResponse);
    }

    @ExceptionHandler(EmailOrPasswordWrongException.class)
    private ResponseEntity<Object> handleEmailNotFoundException(EmailOrPasswordWrongException emailNotFoundException) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorMessageDTO(HttpStatus.NOT_FOUND, emailNotFoundException.getMessage()));
    }

    @ExceptionHandler(EmailAlreadyExistsException.class)
    private ResponseEntity<Object> handleEmailAlreadyExistsException(EmailAlreadyExistsException emailAlreadyExistsException) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorMessageDTO(HttpStatus.INTERNAL_SERVER_ERROR, emailAlreadyExistsException.getMessage()));
    }

    @ExceptionHandler(OwnerNotFoundException.class)
    private ResponseEntity<Object> handleOwnerNotFoundException(OwnerNotFoundException ownerNotFoundException) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorMessageDTO(HttpStatus.NOT_FOUND, ownerNotFoundException.getMessage()));
    }

    @ExceptionHandler(PasswordLengthException.class)
    private ResponseEntity<Object> handlePasswordLengthException(PasswordLengthException passwordLengthException) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorMessageDTO(HttpStatus.BAD_REQUEST, passwordLengthException.getMessage()));
    }

    @ExceptionHandler(PasswordRegexException.class)
    private ResponseEntity<Object> handlePasswordRegexException(PasswordRegexException passwordRegexException) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorMessageDTO(HttpStatus.BAD_REQUEST, passwordRegexException.getMessage()));
    }

    @ExceptionHandler(TripAlreadyConfirmedException.class)
    private ResponseEntity<Object> handleTripAlreadyConfirmedException(TripAlreadyConfirmedException tripAlreadyConfirmedException) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(new ErrorMessageDTO(HttpStatus.CONFLICT, tripAlreadyConfirmedException.getMessage()));
    }

    @ExceptionHandler(TripDateException.class)
    private ResponseEntity<Object> handleTripDateException(TripDateException tripDateException) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorMessageDTO(HttpStatus.BAD_REQUEST, tripDateException.getMessage()));
    }

    @ExceptionHandler(TripNotFoundException.class)
    private ResponseEntity<Object> handleTripNotFoundException(TripNotFoundException tripNotFoundException) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorMessageDTO(HttpStatus.NOT_FOUND, tripNotFoundException.getMessage()));
    }


}
