package com.supremecourt.studentgradingsystem.model.request;

import com.supremecourt.studentgradingsystem.enums.EntranceType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class ResendOTPRequestDto {
    private EntranceType entranceType;
    @Email
    @NotBlank
    private String email;
}
