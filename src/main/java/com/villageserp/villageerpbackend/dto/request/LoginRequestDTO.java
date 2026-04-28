package com.villageserp.villageerpbackend.dto.request;

import jakarta.validation.constraints.NotBlank;

public record LoginRequestDTO(

        @NotBlank(message = "กรุณากรอก Username")
        String username,

        @NotBlank(message = "กรุณากรอก Password")
        String password
) {}
