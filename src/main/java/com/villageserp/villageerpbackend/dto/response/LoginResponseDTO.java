package com.villageserp.villageerpbackend.dto.response;

public record LoginResponseDTO(
        String accessToken,
        String username,
        String email,
        String role,
        boolean isFirstLogin
) {}
