package com.documentservice.exception;

import jakarta.validation.ConstraintViolationException;
import lombok.NonNull;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDateTime;


@RestControllerAdvice
public class AppExceptionHandler extends ResponseEntityExceptionHandler {


    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                                  @NonNull HttpHeaders headers,
                                                                  @NonNull HttpStatusCode status, @NonNull WebRequest request) {

        var message = String.join( ",", ex.getAllErrors()
                .stream().map(DefaultMessageSourceResolvable::getDefaultMessage)
                .toList()
        );
        var exc = AppException.builder()
                .statusCode(status.value())
                .statusCodeMessage(HttpStatus.METHOD_NOT_ALLOWED.name())
                .message(message).
                path(request.getContextPath())
                .time(LocalDateTime.now())
                .build();
        return new ResponseEntity<>(exc, HttpStatus.METHOD_NOT_ALLOWED);
    }
    @ExceptionHandler({ConstraintViolationException.class})
    public  ResponseEntity<?> constrainsFailed(ConstraintViolationException exception, WebRequest request){

        var message = String.join( ",", exception.getConstraintViolations()
                .stream().map(c->   c.getInvalidValue()+" : "+c.getMessage())
                .toList()
        );
        var exc = AppException.builder()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .statusCodeMessage(HttpStatus.BAD_REQUEST.name())
                .message(message).
                path(request.getContextPath())
                .time(LocalDateTime.now())
                .build();
        return new ResponseEntity<>(exc, HttpStatus.BAD_REQUEST);
    }
    @Override
    protected ResponseEntity<Object> handleHttpRequestMethodNotSupported(HttpRequestMethodNotSupportedException ex,
                                                                         @NonNull HttpHeaders headers,
                                                                         @NonNull HttpStatusCode status,
                                                                         @NonNull WebRequest request) {

        var exc = AppException.builder()
                .statusCode(status.value())
                .statusCodeMessage(HttpStatus.METHOD_NOT_ALLOWED.name())
                .message(ex.getMessage()).
                path(request.getContextPath())
                .time(LocalDateTime.now())
                .build();
        return  new ResponseEntity<>(exc, HttpStatus.METHOD_NOT_ALLOWED);
    }
   @ExceptionHandler({InternalServerError.class})
    public  ResponseEntity<?> InternalException(InternalServerError exception, WebRequest request){
       var exc = AppException.builder()
               .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
               .statusCodeMessage(HttpStatus.INTERNAL_SERVER_ERROR.name())
               .message(exception.getMessage()).
               path(request.getContextPath())
               .time(LocalDateTime.now())
               .build();
       return  new ResponseEntity<>(exc, HttpStatus.INTERNAL_SERVER_ERROR);

   }

    @ExceptionHandler(value = {EntityNotFoundException.class})
    public  ResponseEntity<?> userNotFound(EntityNotFoundException exception
    ,WebRequest request){
        var exc = AppException.builder()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .statusCodeMessage(HttpStatus.NOT_FOUND.name())
                .message(exception.getMessage()).
                path(request.getContextPath())
                .time(LocalDateTime.now())
                .build();
        return  new ResponseEntity<>(exc, HttpStatus.NOT_FOUND);
    }
    @ExceptionHandler(value = {EntityAlreadyExistException.class})
    public  ResponseEntity<?>  Exits(EntityAlreadyExistException exception, WebRequest request){
        var exc = AppException.builder()
                .statusCode(HttpStatus.CONFLICT.value())
                .statusCodeMessage(HttpStatus.CONFLICT.name())
                .message(exception.getMessage()).
                path(request.getContextPath())
                .time(LocalDateTime.now())
                .build();
        return  new ResponseEntity<>(exc, HttpStatus.CONFLICT);
    }

 @ExceptionHandler(value = {InvalidDocument.class})
    public  ResponseEntity<?> Invalid(InvalidDocument exception, WebRequest request){
        var exc = AppException.builder()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .statusCodeMessage(HttpStatus.BAD_REQUEST.name())
                .message(exception.getMessage()).
                path(request.getContextPath())
                .time(LocalDateTime.now())
                .build();
        return  new ResponseEntity<>(exc, HttpStatus.BAD_REQUEST);
    }



}
