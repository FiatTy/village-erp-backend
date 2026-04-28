package com.villageserp.villageerpbackend.dto.response;

import java.util.UUID;

public record UserMeResponseDTO(
        UUID id,
        String username,
        String email,
        String role,
        boolean isFirstLogin
) {
}
