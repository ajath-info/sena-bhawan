package com.example.sena_bhawan.advise;

import com.example.sena_bhawan.dto.ErrorResponse;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.InvalidDataAccessResourceUsageException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.ExceptionHandlerExceptionResolver;

import java.sql.SQLException;

@ControllerAdvice
public class GlobalExceptionHandler {

    // ✅ Duplicate / Constraint Violation
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleDuplicateException() {
        return new ResponseEntity<>(
                new ErrorResponse("Duplicate record already exists"),
                HttpStatus.CONFLICT
        );
    }

    // ✅ SQL Exception
    @ExceptionHandler(SQLException.class)
    public ResponseEntity<ErrorResponse> handleSQLException() {
        return new ResponseEntity<>(
                new ErrorResponse("Database error occurred"),
                HttpStatus.INTERNAL_SERVER_ERROR
        );
    }

    // ✅ SQL Exception
    @ExceptionHandler(InvalidDataAccessResourceUsageException.class)
    public ResponseEntity<ErrorResponse> handleSQLException1() {
        return new ResponseEntity<>(
                new ErrorResponse("Database error occurred"),
                HttpStatus.INTERNAL_SERVER_ERROR
        );
    }

    // ✅ Illegal Argument
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgument() {
        return new ResponseEntity<>(
                new ErrorResponse("Invalid request data"),
                HttpStatus.BAD_REQUEST
        );
    }

    // ✅ Generic Exception (Fallback)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException() {
        return new ResponseEntity<>(
                new ErrorResponse("Something went wrong"),
                HttpStatus.INTERNAL_SERVER_ERROR
        );
    }
}