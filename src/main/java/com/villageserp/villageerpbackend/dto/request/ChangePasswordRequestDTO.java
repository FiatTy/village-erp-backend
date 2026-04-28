package com.villageserp.villageerpbackend.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ChangePasswordRequestDTO(

        @NotBlank(message = "กรุณากรอกรหัสผ่านเดิม")
        String oldPassword,

        @NotBlank(message = "กรุณากรอกรหัสผ่านใหม่")
        @Size(min = 8, message = "รหัสผ่านใหม่ต้องมีอย่างน้อย 8 ตัวอักษร")
        String newPassword
) {}
