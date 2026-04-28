package com.villageserp.villageerpbackend.controller;

import com.villageserp.villageerpbackend.common.ApiResponse;
import com.villageserp.villageerpbackend.common.CookieUtil;
import com.villageserp.villageerpbackend.dto.request.ChangePasswordRequestDTO;
import com.villageserp.villageerpbackend.dto.request.LoginRequestDTO;
import com.villageserp.villageerpbackend.dto.response.LoginResponseDTO;
import com.villageserp.villageerpbackend.dto.response.UserMeResponseDTO;
import com.villageserp.villageerpbackend.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponseDTO>> login(
            @Valid @RequestBody LoginRequestDTO request,
            HttpServletResponse response) {

        return ApiResponse.ok(authService.login(request, response));
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<LoginResponseDTO>> refresh(
            HttpServletRequest request) {

        String refreshToken = CookieUtil
                .getRefreshToken(request)
                .orElse(null);

        return ApiResponse.ok(authService.refresh(refreshToken));
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(
            HttpServletRequest request,
            HttpServletResponse response) {

        String refreshToken = CookieUtil
                .getRefreshToken(request)
                .orElse(null);

        authService.logout(refreshToken, response);
        return ApiResponse.ok();
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserMeResponseDTO>> getMe() {
        return ApiResponse.ok(authService.getMe());
    }

    @PatchMapping("/change-password")
    public ResponseEntity<ApiResponse<Void>> changePassword(
            @Valid @RequestBody ChangePasswordRequestDTO request) {
        authService.changePassword(request);
        return ApiResponse.ok();
    }
}
