package com.villageserp.villageerpbackend.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.LocalDateTime;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ApiResponse<T>(
        int status,
        String message,
        T data,
        ErrorDetail error,
        LocalDateTime timestamp,
        String path
) {
    private static String getCurrentPath() {
        try {
            HttpServletRequest request = ((ServletRequestAttributes)
                    RequestContextHolder.currentRequestAttributes())
                    .getRequest();
            return request.getRequestURI();
        } catch (Exception e) {
            return null;
        }
    }

    public static <T> ResponseEntity<ApiResponse<T>> ok(T data) {
        return ResponseEntity.ok(new ApiResponse<>(
                HttpStatus.OK.value(), "สำเร็จ",
                data, null,
                LocalDateTime.now(), getCurrentPath()
        ));
    }

    public static ResponseEntity<ApiResponse<Void>> ok() {
        return ResponseEntity.ok(new ApiResponse<>(
                HttpStatus.OK.value(), "สำเร็จ",
                null, null,
                LocalDateTime.now(), getCurrentPath()
        ));
    }

    public static <T> ResponseEntity<ApiResponse<T>> created(T data) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>(
                        HttpStatus.CREATED.value(), "สร้าง สำเร็จ",
                        data, null,
                        LocalDateTime.now(), getCurrentPath()
                ));
    }

    public static <T> ResponseEntity<ApiResponse<T>> updated(T data) {
        return ResponseEntity.ok(new ApiResponse<>(
                HttpStatus.OK.value(), "แก้ไข สำเร็จ",
                data, null,
                LocalDateTime.now(), getCurrentPath()
        ));
    }

    public static ResponseEntity<ApiResponse<Void>> deleted() {
        return ResponseEntity.ok(new ApiResponse<>(
                HttpStatus.OK.value(), "ลบ สำเร็จ",
                null, null,
                LocalDateTime.now(), getCurrentPath()
        ));
    }

    // Error Methods → message = null (JsonInclude ซ่อนให้)
    public static ResponseEntity<ApiResponse<Void>> badRequest(
            String message) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ApiResponse<>(
                        HttpStatus.BAD_REQUEST.value(), null,
                        null, new ErrorDetail("BAD_REQUEST", message),
                        LocalDateTime.now(), getCurrentPath()
                ));
    }

    public static ResponseEntity<ApiResponse<Void>> unauthorized(
            String message) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new ApiResponse<>(
                        HttpStatus.UNAUTHORIZED.value(), null,
                        null, new ErrorDetail("UNAUTHORIZED", message),
                        LocalDateTime.now(), getCurrentPath()
                ));
    }

    public static ResponseEntity<ApiResponse<Void>> forbidden(
            String message) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(new ApiResponse<>(
                        HttpStatus.FORBIDDEN.value(), null,
                        null, new ErrorDetail("FORBIDDEN", message),
                        LocalDateTime.now(), getCurrentPath()
                ));
    }

    public static ResponseEntity<ApiResponse<Void>> notFound(
            String message) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ApiResponse<>(
                        HttpStatus.NOT_FOUND.value(), null,
                        null, new ErrorDetail("NOT_FOUND", message),
                        LocalDateTime.now(), getCurrentPath()
                ));
    }

    public static ResponseEntity<ApiResponse<Void>> error(
            HttpStatus status, String code, String message) {
        return ResponseEntity.status(status)
                .body(new ApiResponse<>(
                        status.value(), null,
                        null, new ErrorDetail(code, message),
                        LocalDateTime.now(), getCurrentPath()
                ));
    }

    public static ResponseEntity<ApiResponse<Void>> internalError() {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponse<>(
                        HttpStatus.INTERNAL_SERVER_ERROR.value(), null,
                        null, new ErrorDetail(
                        "INTERNAL_ERROR",
                        "เกิดข้อผิดพลาด กรุณาลองใหม่อีกครั้ง"),
                        LocalDateTime.now(), getCurrentPath()
                ));
    }

    public record ErrorDetail(String code, String message) {}
}
