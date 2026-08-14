package com.supremecourt.studentgradingsystem.config;

import com.supremecourt.studentgradingsystem.dao.entity.ClaimEntity;
import com.supremecourt.studentgradingsystem.dao.entity.RoleEntity;
import com.supremecourt.studentgradingsystem.dao.entity.RolesClaimsEntity;
import com.supremecourt.studentgradingsystem.dao.entity.UserEntity;
import com.supremecourt.studentgradingsystem.dao.repository.ClaimRepository;
import com.supremecourt.studentgradingsystem.dao.repository.RoleRepository;
import com.supremecourt.studentgradingsystem.dao.repository.RolesClaimsRepository;
import com.supremecourt.studentgradingsystem.dao.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

@Configuration
@RequiredArgsConstructor
public class DbConfig {

    private final RoleRepository roleRepository;
    private final ClaimRepository claimRepository;
    private final RolesClaimsRepository rolesClaimsRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Bean
    CommandLineRunner seedData() {
        return args -> seed();
    }

    @Transactional
    void seed() {
        // 1) Roles
        RoleEntity adminRole = roleRepository.findByName("ADMIN")
                .orElseGet(() -> roleRepository.save(RoleEntity.builder().name("ADMIN").build()));

        roleRepository.findByName("USER")
                .orElseGet(() -> roleRepository.save(RoleEntity.builder().name("USER").build()));

        // 2) Claims
        ClaimEntity createClaim = claimRepository.findByName("create")
                .orElseGet(() -> claimRepository.save(ClaimEntity.builder().name("create").build()));

        ClaimEntity updateClaim = claimRepository.findByName("update")
                .orElseGet(() -> claimRepository.save(ClaimEntity.builder().name("update").build()));

        ClaimEntity deleteClaim = claimRepository.findByName("delete")
                .orElseGet(() -> claimRepository.save(ClaimEntity.builder().name("delete").build()));

        ClaimEntity roleCreateClaim = claimRepository.findByName("role_create")
                .orElseGet(() -> claimRepository.save(ClaimEntity.builder().name("role_create").build()));
        ClaimEntity roleReadClaim = claimRepository.findByName("role_read")
                .orElseGet(() -> claimRepository.save(ClaimEntity.builder().name("role_read").build()));
        ClaimEntity roleUpdateClaim = claimRepository.findByName("role_update")
                .orElseGet(() -> claimRepository.save(ClaimEntity.builder().name("role_update").build()));
        ClaimEntity roleDeleteClaim = claimRepository.findByName("role_delete")
                .orElseGet(() -> claimRepository.save(ClaimEntity.builder().name("role_delete").build()));
        ClaimEntity roleClaimReadMatrix = claimRepository.findByName("role_claim_read_matrix")
                .orElseGet(() -> claimRepository.save(ClaimEntity.builder().name("role_claim_read_matrix").build()));
        ClaimEntity roleClaimUpdateMatrix = claimRepository.findByName("role_claim_update_matrix")
                .orElseGet(() -> claimRepository.save(ClaimEntity.builder().name("role_claim_update_matrix").build()));

        // 3) Role-Claim mapping (ADMIN -> create, update, delete)
        ensureRoleClaim(adminRole, createClaim);
        ensureRoleClaim(adminRole, updateClaim);
        ensureRoleClaim(adminRole, deleteClaim);

        ensureRoleClaim(adminRole, roleCreateClaim);
        ensureRoleClaim(adminRole, roleReadClaim);
        ensureRoleClaim(adminRole, roleUpdateClaim);
        ensureRoleClaim(adminRole, roleDeleteClaim);

        ensureRoleClaim(adminRole, roleClaimReadMatrix);
        ensureRoleClaim(adminRole, roleClaimUpdateMatrix);

        // 4) Adding a user with Admin role
        if (userRepository.count() == 0 && roleRepository.findByName("ADMIN").isPresent()) {
            UserEntity user = UserEntity.builder()
                    .firstName("Admin")
                    .lastName("Admin")
                    .username("admin")
                    .email("admin@supremetest.az")
                    .password(passwordEncoder.encode("admin"))
                    .role(adminRole)
                    .build();
            userRepository.save(user);
        }
    }

    private void ensureRoleClaim(RoleEntity role, ClaimEntity claim) {
        boolean exists = rolesClaimsRepository.existsByRoleAndClaim(role, claim);
        if (!exists) {
            rolesClaimsRepository.save(
                    RolesClaimsEntity.builder()
                            .role(role)
                            .claim(claim)
                            .build()
            );
        }
    }
}