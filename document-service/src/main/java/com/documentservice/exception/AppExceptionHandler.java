package com.documentservice.exception;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import lombok.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDateTime;


@RestControllerAdvice
public class AppExceptionHandler extends ResponseEntityExceptionHandler {

    private  static  final Logger logger = LoggerFactory.getLogger(AppExceptionHandler.class);
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                                  @NonNull HttpHeaders headers,
                                                                  @NonNull HttpStatusCode status, @NonNull WebRequest request) {

        var message = String.join( ",", ex.getAllErrors()
                .stream().map(DefaultMessageSourceResolvable::getDefaultMessage)
                .toList()
        );
        var exc = AppException.builder()
                .status(status.value())
                .statusCodeMessage(HttpStatus.METHOD_NOT_ALLOWED.name())
                .message(message).
                path(((ServletWebRequest)request).getRequest().getRequestURI())
                .timestamp(LocalDateTime.now().toString())
                .build();
        logger.error("Method argument not valid exception: {},HttpStatus: {}, trace-Id: {} ", exc,status,headers.get("trace-Id"));
        return new ResponseEntity<>(exc, HttpStatus.METHOD_NOT_ALLOWED);
    }
    @ExceptionHandler({ConstraintViolationException.class})
    public  ResponseEntity<?> constrainsFailed(ConstraintViolationException exception, WebRequest request){

        var message = String.join( ",", exception.getConstraintViolations()
                .stream().map(c->   c.getInvalidValue()+" : "+c.getMessage())
                .toList()
        );
        var exc = AppException.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .statusCodeMessage(HttpStatus.BAD_REQUEST.name())
                .message(message).
                path(((ServletWebRequest)request).getRequest().getRequestURI())
                .timestamp(LocalDateTime.now().toString())
                .build();
        logger.error("Field not valid exception: {}, trace-Id: {} ", exc,request.getHeader("trace-Id"));

        return new ResponseEntity<>(exc, HttpStatus.BAD_REQUEST);
    }
    @Override
    protected ResponseEntity<Object> handleHttpRequestMethodNotSupported(HttpRequestMethodNotSupportedException ex,
                                                                         @NonNull HttpHeaders headers,
                                                                         @NonNull HttpStatusCode status,
                                                                         @NonNull WebRequest request) {

        var exc = AppException.builder()
                .status(status.value())
                .statusCodeMessage(HttpStatus.METHOD_NOT_ALLOWED.name())
                .message(ex.getMessage()).
                path(((ServletWebRequest)request).getRequest().getRequestURI())
                .timestamp(LocalDateTime.now().toString())
                .build();
        logger.error("Method not supported exception: {},HttpStatus: {} , trace-Id: {} ", exc,status,request.getHeader("trace-Id"));
        return  new ResponseEntity<>(exc, HttpStatus.METHOD_NOT_ALLOWED);
    }
   @ExceptionHandler({InternalServerError.class})
    public  ResponseEntity<?> InternalException(InternalServerError exception, HttpServletRequest request){
       var exc = AppException.builder()
               .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
               .statusCodeMessage(HttpStatus.INTERNAL_SERVER_ERROR.name())
               .message(exception.getMessage()).
               path(request.getContextPath())
               .timestamp(LocalDateTime.now().toString())
               .build();
       logger.error("Internal server exception exception: {} , trace-Id: {} ", exc,request.getHeader("trace-Id"));
       return  new ResponseEntity<>(exc, HttpStatus.INTERNAL_SERVER_ERROR);

   }

    @ExceptionHandler(value = {EntityNotFoundException.class})
    public  ResponseEntity<?> userNotFound(EntityNotFoundException exception
    , final HttpServletRequest request){
        var exc = AppException.builder()
                .status(HttpStatus.NOT_FOUND.value())
                .statusCodeMessage(HttpStatus.NOT_FOUND.name())
                .message(exception.getMessage()).
                path(request.getRequestURI())
                .timestamp(LocalDateTime.now().toString())
                .build();
        logger.error("Entity not found exception: {}, trace-Id: {} ", exc,request.getHeader("trace-Id"));
        return  new ResponseEntity<>(exc, HttpStatus.NOT_FOUND);
    }
    @ExceptionHandler(value = {EntityAlreadyExistException.class})
    public  ResponseEntity<?>  Exits(EntityAlreadyExistException exception, HttpServletRequest request){
        var exc = AppException.builder()
                .status(HttpStatus.CONFLICT.value())
                .statusCodeMessage(HttpStatus.CONFLICT.name())
                .message(exception.getMessage()).
                path(request.getContextPath())
                .timestamp(LocalDateTime.now().toString())
                .build();
        logger.error("Duplicated entity  exception: {}, trace-Id: {} ", exc,request.getHeader("trace-Id"));
        return  new ResponseEntity<>(exc, HttpStatus.CONFLICT);
    }

 @ExceptionHandler(value = {InvalidDocument.class})
    public  ResponseEntity<?> Invalid(InvalidDocument exception, HttpServletRequest request){
        var exc = AppException.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .statusCodeMessage(HttpStatus.BAD_REQUEST.name())
                .message(exception.getMessage()).
                 path(request.getContextPath())
                .timestamp(LocalDateTime.now().toString())
                .build();
     logger.error("Invalid entity  exception: {}, trace-Id: {} ", exc,request.getHeader("trace-Id"));
     return  new ResponseEntity<>(exc, HttpStatus.BAD_REQUEST);
    }



}
