package com.villageserp.villageerpbackend.service;

import com.villageserp.villageerpbackend.common.CookieUtil;
import com.villageserp.villageerpbackend.config.JwtConfig;
import com.villageserp.villageerpbackend.dto.request.ChangePasswordRequestDTO;
import com.villageserp.villageerpbackend.dto.request.LoginRequestDTO;
import com.villageserp.villageerpbackend.dto.response.LoginResponseDTO;
import com.villageserp.villageerpbackend.dto.response.UserMeResponseDTO;
import com.villageserp.villageerpbackend.entity.UserEntity;
import com.villageserp.villageerpbackend.exception.BusinessException;
import com.villageserp.villageerpbackend.exception.ResourceNotFoundException;
import com.villageserp.villageerpbackend.repository.UserRepository;
import com.villageserp.villageerpbackend.security.JwtUtil;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true) // default read-only
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final JwtConfig jwtConfig;
    private final RedisService redisService;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public LoginResponseDTO login(
            LoginRequestDTO request,
            HttpServletResponse response) {

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.username(),
                        request.password()
                )
        );

        UserEntity user = findUserByUsername(request.username());

        String accessToken = jwtUtil.generateAccessToken(
                user.getUsername(),
                user.getRole().name()
        );
        String refreshToken = jwtUtil.generateRefreshToken(
                user.getUsername()
        );

        long refreshExpirationSeconds = jwtConfig.getRefreshExpiration() / 1000;
        redisService.saveRefreshToken(
                user.getUsername(),
                refreshToken,
                refreshExpirationSeconds
        );

        response.addHeader("Set-Cookie",
                CookieUtil.createRefreshTokenCookie(
                        refreshToken,
                        refreshExpirationSeconds
                ).toString()
        );

        return toLoginResponse(user, accessToken);
    }

    public LoginResponseDTO refresh(String refreshToken) {

        validateRefreshTokenNotEmpty(refreshToken);

        if (!jwtUtil.isTokenValid(refreshToken)) {
            throw new BusinessException(
                    "INVALID_REFRESH_TOKEN",
                    "Refresh Token ไม่ถูกต้องหรือหมดอายุ"
            );
        }

        String username = jwtUtil.extractUsername(refreshToken);

        if (!redisService.validateRefreshToken(username, refreshToken)) {
            throw new BusinessException(
                    "INVALID_REFRESH_TOKEN",
                    "Refresh Token ไม่ถูกต้อง กรุณา Login ใหม่"
            );
        }

        UserEntity user = findUserByUsername(username);

        String newAccessToken = jwtUtil.generateAccessToken(
                user.getUsername(),
                user.getRole().name()
        );

        return toLoginResponse(user, newAccessToken);
    }

    @Transactional
    public void logout(
            String refreshToken,
            HttpServletResponse response) {

        if (refreshToken != null
                && !refreshToken.isBlank()
                && jwtUtil.isTokenValid(refreshToken)) {

            String username = jwtUtil.extractUsername(refreshToken);
            redisService.deleteRefreshToken(username);
        }

        response.addHeader("Set-Cookie",
                CookieUtil.deleteRefreshTokenCookie().toString()
        );
    }

    public UserMeResponseDTO getMe() {

        String username = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        UserEntity user = findUserByUsername(username);

        return new UserMeResponseDTO(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getRole().name(),
                user.isFirstLogin()
        );
    }

    @Transactional
    public void changePassword(ChangePasswordRequestDTO request) {

        String username = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        UserEntity user = findUserByUsername(username);

        if (!passwordEncoder.matches(
                request.oldPassword(),
                user.getPasswordHash())) {
            throw new BusinessException(
                    "INVALID_PASSWORD",
                    "รหัสผ่านเดิมไม่ถูกต้อง"
            );
        }

        if (passwordEncoder.matches(
                request.newPassword(),
                user.getPasswordHash())) {
            throw new BusinessException(
                    "SAME_PASSWORD",
                    "รหัสผ่านใหม่ต้องไม่ซ้ำกับรหัสผ่านเดิม"
            );
        }

        user.setPasswordHash(
                passwordEncoder.encode(request.newPassword())
        );

        user.setFirstLogin(false);
        userRepository.save(user);
    }

    private UserEntity findUserByUsername(String username) {
        return userRepository
                .findByUsernameAndIsDeletedFalse(username)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User", username)
                );
    }

    private void validateRefreshTokenNotEmpty(String refreshToken) {
        if (refreshToken == null || refreshToken.isBlank()) {
            throw new BusinessException(
                    "MISSING_REFRESH_TOKEN",
                    "กรุณา Login ใหม่อีกครั้ง"
            );
        }
    }

    private LoginResponseDTO toLoginResponse(
            UserEntity user,
            String accessToken) {
        return new LoginResponseDTO(
                accessToken,
                user.getUsername(),
                user.getEmail(),
                user.getRole().name(),
                user.isFirstLogin()
        );
    }
}
