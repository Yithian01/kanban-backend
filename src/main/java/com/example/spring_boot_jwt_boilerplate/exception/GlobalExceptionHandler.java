package com.example.spring_boot_jwt_boilerplate.exception;

import com.example.spring_boot_jwt_boilerplate.dto.common.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 1. @Valid 검증 실패 시 (400 Bad Request)
     * DTO의 어노테이션(@NotNull, @Email 등) 위반 시 발생
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleValidationException(MethodArgumentNotValidException ex) {
        BindingResult bindingResult = ex.getBindingResult();
        Map<String, String> errors = new HashMap<>();

        for (FieldError fieldError : bindingResult.getFieldErrors()) {
            errors.put(fieldError.getField(), fieldError.getDefaultMessage());
        }

        return ResponseEntity
                .status(ErrorCode.INVALID_INPUT_VALUE.getStatus())
                .body(ApiResponse.fail(ErrorCode.INVALID_INPUT_VALUE.getMessage(), errors));
    }

    /**
     * 2. 비즈니스 로직 예외 (CustomException)
     * 개발자가 의도적으로 발생시킨 예외 (예: 중복 이메일, 회원 없음 등)
     */
    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ApiResponse<Void>> handleCustomException(CustomException ex) {
        return ResponseEntity
                .status(ex.getErrorCode().getStatus())
                .body(ApiResponse.fail(ex.getErrorCode().getMessage()));
    }

    /**
     * 3. 나머지 모든 예외 (500 Server Error)
     * 예상치 못한 에러
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<String>> handleException(Exception ex) {
        // 실제 운영에서는 에러 로그를 남겨야 함 (log.error("Error", ex))
        return ResponseEntity
                .status(ErrorCode.INTERNAL_SERVER_ERROR.getStatus())
                .body(ApiResponse.fail(ErrorCode.INTERNAL_SERVER_ERROR.getMessage(), ex.getMessage()));
    }
}