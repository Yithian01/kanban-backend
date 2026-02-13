package com.example.spring_boot_jwt_boilerplate.dto.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ApiResponse<T> {

    private final boolean success;
    private final String message;
    private final T data;

    /**
     * 1. 데이터가 있는 성공 응답
     * 예: 조회 성공, 로그인 성공
     */
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(true, "요청이 성공적으로 처리되었습니다.", data);
    }

    /**
     * 2. 데이터가 없는 성공 응답 (void)
     * 예: 로그아웃, 회원탈퇴, 단순 상태 변경
     */
    public static <T> ApiResponse<T> success() {
        return new ApiResponse<>(true, "요청이 성공적으로 처리되었습니다.", null);
    }

    /**
     * 3. 커스텀 메시지 + 데이터 성공 응답 (옵션)
     * 예: "사용 가능한 아이디입니다." 같은 특정 메시지가 필요할 때
     */
    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>(true, message, data);
    }

    /**
     * 4. 실패 응답 (메시지만)
     * 예: 일반적인 에러 메시지 반환
     */
    public static <T> ApiResponse<T> fail(String message) {
        return new ApiResponse<>(false, message, null);
    }

    /**
     * 5. 실패 응답 (메시지 + 에러 데이터)
     * 예: 유효성 검사 실패 시 "어떤 필드가 틀렸는지" Map으로 반환할 때 사용
     */
    public static <T> ApiResponse<T> fail(String message, T data) {
        return new ApiResponse<>(false, message, data);
    }
}