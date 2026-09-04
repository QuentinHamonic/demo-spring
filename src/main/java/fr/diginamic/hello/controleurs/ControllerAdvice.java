package fr.diginamic.hello.controleurs;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import fr.diginamic.hello.exceptions.DepartementException;
import fr.diginamic.hello.exceptions.VilleException;

@RestControllerAdvice
public class ControllerAdvice {

    @ExceptionHandler(VilleException.class)
    public ResponseEntity<String> handleVilleException(VilleException exception) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exception.getMessage());
    }

    @ExceptionHandler(DepartementException.class)
    public ResponseEntity<String> handleDepartementException(DepartementException exception) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exception.getMessage());
    }

}
