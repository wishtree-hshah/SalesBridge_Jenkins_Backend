package com.wishtreetech.salesbridge.common.config;

import com.wishtreetech.rocketreach.exception.ApiResponseException;
import com.wishtreetech.commonutils.dto.ErrorResponse;
import com.wishtreetech.commonutils.dto.ResponseDto;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.time.LocalDateTime;

/**
 * The type Global exception handler.
 */
@ControllerAdvice
public class GlobalExceptionHanler {

    /**
     * Handle expired jwt exception response entity.
     *
     * @param e       the e
     * @param request the request
     * @return the response entity
     */
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(ExpiredJwtException.class)
    public ResponseEntity<ResponseDto<ErrorResponse>> handleExpiredJwtException(ExpiredJwtException e, HttpServletRequest request) {
        return buildErrorResponse(HttpStatus.UNAUTHORIZED, e.getMessage(), request);
    }

    /**
     * Handle generic runtime exceptions
     *
     * @param ex      the ex
     * @param request the request
     * @return response entity
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ResponseDto<ErrorResponse>> handleRuntimeException(RuntimeException ex, HttpServletRequest request) {
        return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage(), request);
    }

    /**
     * Handle access exception response entity.
     *
     * @param ex      the ex
     * @param request the request
     * @return the response entity
     */
    @ExceptionHandler(AuthorizationDeniedException.class)
    public ResponseEntity<ResponseDto<ErrorResponse>> handleAccessException(AuthorizationDeniedException ex, HttpServletRequest request) {
        return buildErrorResponse(HttpStatus.UNAUTHORIZED, ex.getMessage(), request);
    }

    /**
     * Handle json parse exception response entity.
     *
     * @param ex      the ex
     * @param request the request
     * @return the response entity
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ResponseDto<ErrorResponse>> handleJsonParseException(HttpMessageNotReadableException ex, HttpServletRequest request) {
        return buildErrorResponse(HttpStatus.BAD_REQUEST, ex.getMessage(), request);
    }

    /**
     * Handle API response exceptions
     *
     * @param ex
     * @param request
     * @return
     */
    @ExceptionHandler(ApiResponseException.class)
    public ResponseEntity<ResponseDto<ErrorResponse>> handleApiResponseException(ApiResponseException ex, HttpServletRequest request) {
        return buildErrorResponse(HttpStatus.BAD_REQUEST, ex.getMessage(), request);
    }

    /**
     * Generic method to create an error response wrapped inside ResponseDto
     *
     * @param status
     * @param message
     * @param request
     * @return
     */
    private ResponseEntity<ResponseDto<ErrorResponse>> buildErrorResponse(HttpStatus status, String message, HttpServletRequest request) {
        ErrorResponse errorResponse = new ErrorResponse(
                LocalDateTime.now(),
                status.value(),
                status.getReasonPhrase(),
                message,
                request.getRequestURI()
        );

        ResponseDto<ErrorResponse> responseDto = new ResponseDto<>(false, errorResponse);
        return new ResponseEntity<>(responseDto, status);
    }
}
