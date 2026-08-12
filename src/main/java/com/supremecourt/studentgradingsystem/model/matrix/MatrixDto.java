package com.supremecourt.studentgradingsystem.model.matrix;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@FieldDefaults(level= AccessLevel.PRIVATE)
public class MatrixDto {
    List<RoleClaimGetDto> rolesClaims;
    List<RoleGetDto> roles;
    List<ClaimGetDto> claims;
}
