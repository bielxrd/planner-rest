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

    @ExceptionHandler(NotFoundException.class)
    private ResponseEntity<Object> handleNotFoundException(NotFoundException notFoundException) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorMessageDTO(HttpStatus.NOT_FOUND, notFoundException.getMessage()));
    }

    @ExceptionHandler(AlreadyExistsException.class)
    private ResponseEntity<Object> handleAlreadyExistsException(AlreadyExistsException alreadyExistsException) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorMessageDTO(HttpStatus.INTERNAL_SERVER_ERROR, alreadyExistsException.getMessage()));
    }

    @ExceptionHandler(InvalidInputException.class)
    private ResponseEntity<Object> handlePasswordLengthException(InvalidInputException invalidInputException) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorMessageDTO(HttpStatus.BAD_REQUEST, invalidInputException.getMessage()));
    }

    @ExceptionHandler(TripAlreadyConfirmedException.class)
    private ResponseEntity<Object> handleTripAlreadyConfirmedException(TripAlreadyConfirmedException tripAlreadyConfirmedException) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(new ErrorMessageDTO(HttpStatus.CONFLICT, tripAlreadyConfirmedException.getMessage()));
    }


}
