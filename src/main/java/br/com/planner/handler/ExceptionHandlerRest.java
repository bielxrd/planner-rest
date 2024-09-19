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
    private ResponseEntity<ErrorMessageDTO> handleTokenInvalidException(TokenInvalidException tokenInvalidException) {
        ErrorMessageDTO threatResponse = new ErrorMessageDTO(403, tokenInvalidException.getMessage());
        return ResponseEntity.status(threatResponse.getStatus()).body(threatResponse);
    }

    @ExceptionHandler(NotFoundException.class)
    private ResponseEntity<Object> handleNotFoundException(NotFoundException notFoundException) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorMessageDTO(404, notFoundException.getMessage()));
    }

    @ExceptionHandler(AlreadyExistsException.class)
    private ResponseEntity<Object> handleAlreadyExistsException(AlreadyExistsException alreadyExistsException) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorMessageDTO(500, alreadyExistsException.getMessage()));
    }

    @ExceptionHandler(InvalidInputException.class)
    private ResponseEntity<Object> handlePasswordLengthException(InvalidInputException invalidInputException) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorMessageDTO(400, invalidInputException.getMessage()));
    }

    @ExceptionHandler(TripAlreadyConfirmedException.class)
    private ResponseEntity<Object> handleTripAlreadyConfirmedException(TripAlreadyConfirmedException tripAlreadyConfirmedException) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(new ErrorMessageDTO(410, tripAlreadyConfirmedException.getMessage()));
    }
}
