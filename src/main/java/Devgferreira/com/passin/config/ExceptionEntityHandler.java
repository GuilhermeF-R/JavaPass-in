package Devgferreira.com.passin.config;

import Devgferreira.com.passin.domain.attendee.exceptions.AttendeeAlreadyRegisteredException;
import Devgferreira.com.passin.domain.attendee.exceptions.AttendeeNotFoundException;
import Devgferreira.com.passin.domain.checkin.exceptions.CheckInAlreadyExistsException;
import Devgferreira.com.passin.domain.event.exceptions.EventIsFullException;
import Devgferreira.com.passin.domain.event.exceptions.EventNotFoundException;
import Devgferreira.com.passin.dto.gerenal.ErrorResponseDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ExceptionEntityHandler {
    @ExceptionHandler(EventNotFoundException.class)
    public ResponseEntity handleEventNotFound(EventNotFoundException exception){
        return ResponseEntity.notFound().build();
    }

    @ExceptionHandler(EventIsFullException.class)
    public ResponseEntity<ErrorResponseDTO> handleEventIsFull(EventIsFullException exception){
        return ResponseEntity.badRequest().body(new ErrorResponseDTO(exception.getMessage()));
    }
    @ExceptionHandler(AttendeeNotFoundException.class)
    public ResponseEntity handleAttendeeNotFound(AttendeeNotFoundException exception){
        return ResponseEntity.notFound().build();
    }

    @ExceptionHandler(AttendeeAlreadyRegisteredException.class)
    public ResponseEntity handleAttendeeAlreadyExists(AttendeeAlreadyRegisteredException exception){
        return ResponseEntity.status(HttpStatus.CONFLICT).build();
    }
    @ExceptionHandler(CheckInAlreadyExistsException.class)
    public ResponseEntity handleCheckInAlreadyExists(CheckInAlreadyExistsException exception){
        return ResponseEntity.status(HttpStatus.CONFLICT).build();
    }

}
