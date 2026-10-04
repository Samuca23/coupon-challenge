package com.samuelchiodini.couponchallenge.coupon.infrastructure.web;

import com.samuelchiodini.couponchallenge.coupon.domain.exceptions.CouponAlreadyDeletedException;
import com.samuelchiodini.couponchallenge.coupon.domain.exceptions.CouponNotFoundException;
import com.samuelchiodini.couponchallenge.coupon.domain.exceptions.InvalidCouponCodeException;
import com.samuelchiodini.couponchallenge.coupon.domain.exceptions.InvalidDiscountValueException;
import com.samuelchiodini.couponchallenge.coupon.domain.exceptions.InvalidExpirationDateException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex) {
        List<String> fields = ex.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getField)
                .distinct()
                .toList();

        return ResponseEntity.badRequest()
                .body(ErrorResponse.of("VALIDATION_ERROR", "campo(s) obrigatório(s) ausente(s) ou inválido(s)", fields));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleMalformedRequest(HttpMessageNotReadableException ex) {
        return ResponseEntity.badRequest()
                .body(ErrorResponse.of("MALFORMED_REQUEST", "corpo da requisição malformado"));
    }

    @ExceptionHandler(InvalidCouponCodeException.class)
    public ResponseEntity<ErrorResponse> handleInvalidCode(InvalidCouponCodeException ex) {
        return ResponseEntity.badRequest()
                .body(ErrorResponse.of("INVALID_COUPON_CODE", ex.getMessage()));
    }

    @ExceptionHandler(InvalidDiscountValueException.class)
    public ResponseEntity<ErrorResponse> handleInvalidDiscount(InvalidDiscountValueException ex) {
        return ResponseEntity.badRequest()
                .body(ErrorResponse.of("INVALID_DISCOUNT_VALUE", ex.getMessage()));
    }

    @ExceptionHandler(InvalidExpirationDateException.class)
    public ResponseEntity<ErrorResponse> handleInvalidExpiration(InvalidExpirationDateException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ErrorResponse.of("INVALID_EXPIRATION_DATE", ex.getMessage()));
    }

    @ExceptionHandler(CouponNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleCouponNotFound(CouponNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ErrorResponse.of("COUPON_NOT_FOUND", ex.getMessage()));
    }

    @ExceptionHandler(CouponAlreadyDeletedException.class)
    public ResponseEntity<ErrorResponse> handleCouponAlreadyDeleted(CouponAlreadyDeletedException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(ErrorResponse.of("COUPON_ALREADY_DELETED", ex.getMessage()));
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        return ResponseEntity.badRequest()
                .body(ErrorResponse.of("MALFORMED_REQUEST", "parâmetro '" + ex.getName() + "' inválido"));
    }
}
