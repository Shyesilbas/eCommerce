package com.serhat.security.exception;

import com.serhat.security.dto.response.AuthResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler{
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<AuthResponse> handleAuthenticationException(AuthenticationException ex) {
        log.error("Authentication error: {}", ex.getMessage());
        return ResponseEntity.badRequest()
                .body(AuthResponse.builder()
                        .message(ex.getMessage())
                        .build());
    }
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        BindingResult bindingResult = ex.getBindingResult();

        for (FieldError fieldError : bindingResult.getFieldErrors()) {
            errors.put(fieldError.getField(), fieldError.getDefaultMessage());
        }

        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler(InvalidTokenFormat.class)
    public ResponseEntity<ErrorResponse> handleInvalidTokenFormatException(InvalidTokenFormat e){

        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.FORBIDDEN.value(),
                "Invalid Token Format",
                "Token expired or Black listed , try again",
                LocalDateTime.now()
        );
        return new ResponseEntity<>(errorResponse,HttpStatus.FORBIDDEN);
    }
    @ExceptionHandler(EmailAlreadyExistException.class)
    public ResponseEntity<ErrorResponse> handleEmailAlreadyExistException(EmailAlreadyExistException e){

        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                "BAD REQUEST!",
                "Email exists",
                LocalDateTime.now()
        );
        return new ResponseEntity<>(errorResponse,HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UsernameAlreadyExists.class)
    public ResponseEntity<ErrorResponse> handleUsernameAlreadyExists(UsernameAlreadyExists e){

        ErrorResponse errorResponse = new ErrorResponse(

                HttpStatus.BAD_REQUEST.value(),
                "BAD REQUEST",
                "Username Exists!",
                LocalDateTime.now()
        );
        return new ResponseEntity<>(errorResponse,HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleInvalidCredentialsException(InvalidCredentialsException e){

        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                "BAD REQUEST",
                "Invalid Credentials!",
                LocalDateTime.now()
        );
        return new ResponseEntity<>(errorResponse,HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(AddressNotBelongToUserException.class)
    public ResponseEntity<ErrorResponse> handleAddressNotBelongToUserException(AddressNotBelongToUserException e){

        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                "BAD REQUEST",
                "Address Not belongs to you!",
                LocalDateTime.now()
        );
        return new ResponseEntity<>(errorResponse,HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(EmptyShoppingCardException.class)
    public ResponseEntity<ErrorResponse> handleEmptyShoppingCardException(EmptyShoppingCardException e){

        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.NO_CONTENT.value(),
                "NOT FOUND",
                "Shopping Card is empty!",
                LocalDateTime.now()
        );
        return new ResponseEntity<>(errorResponse,HttpStatus.NO_CONTENT);
    }

    @ExceptionHandler(ProductNotFoundInCardException.class)
    public ResponseEntity<ErrorResponse> handleProductNotFoundInCardException(ProductNotFoundInCardException e){

        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                "BAD REQUEST",
                "Product Not Found in Shopping Card!",
                LocalDateTime.now()
        );
        return new ResponseEntity<>(errorResponse,HttpStatus.NO_CONTENT);
    }

    @ExceptionHandler(AddressNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleAddressNotFoundException(AddressNotFoundException e){

        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.NO_CONTENT.value(),
                "NOT FOUND",
                "Address Not Found",
                LocalDateTime.now()
        );
        return new ResponseEntity<>(errorResponse,HttpStatus.NO_CONTENT);
    }

    @ExceptionHandler(OrderNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleOrderNotFoundException(OrderNotFoundException e){

        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.NO_CONTENT.value(),
                "NOT FOUND",
                "Order Not Found",
                LocalDateTime.now()
        );
        return new ResponseEntity<>(errorResponse,HttpStatus.NO_CONTENT);
    }

    @ExceptionHandler(ProductNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleProductNotFoundException(ProductNotFoundException e){

        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.NO_CONTENT.value(),
                "NOT FOUND",
                "Product Not Found",
                LocalDateTime.now()
        );
        return new ResponseEntity<>(errorResponse,HttpStatus.NO_CONTENT);
    }

    @ExceptionHandler(NoOrderException.class)
    public ResponseEntity<ErrorResponse> handleNoOrderException(NoOrderException e){

        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.NO_CONTENT.value(),
                "NOT FOUND",
                "No order found",
                LocalDateTime.now()
        );
        return new ResponseEntity<>(errorResponse,HttpStatus.NO_CONTENT);
    }

    @ExceptionHandler(FavoriteProductNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleFavoriteProductNotFoundException(FavoriteProductNotFoundException e){

        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.NO_CONTENT.value(),
                "NOT FOUND",
                "Favorite Product Not Found",
                LocalDateTime.now()
        );
        return new ResponseEntity<>(errorResponse,HttpStatus.NO_CONTENT);
    }

    @ExceptionHandler(NoNotificationsFoundException.class)
    public ResponseEntity<ErrorResponse> handleNoNotificationsFoundException(NoNotificationsFoundException e){

        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.NO_CONTENT.value(),
                "NOT FOUND",
                "No notification found",
                LocalDateTime.now()
        );
        return new ResponseEntity<>(errorResponse,HttpStatus.NO_CONTENT);
    }

    @ExceptionHandler(EmptyFavoriteListException.class)
    public ResponseEntity<ErrorResponse> handleNoNotificationsFoundException(EmptyFavoriteListException e){

        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.NO_CONTENT.value(),
                "NO CONTENT",
                "Favorite List is empty",
                LocalDateTime.now()
        );
        return new ResponseEntity<>(errorResponse,HttpStatus.NO_CONTENT);
    }

    @ExceptionHandler(TokenNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleTokenNotFoundException(TokenNotFoundException e){

        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "INTERNAL SERVER ERROR",
                "Token Not Found!",
                LocalDateTime.now()
        );
        return new ResponseEntity<>(errorResponse,HttpStatus.INTERNAL_SERVER_ERROR);
    }



}
