package com.supremecourt.studentgradingsystem.config;

import com.supremecourt.studentgradingsystem.dao.entity.ClaimsEntity;
import com.supremecourt.studentgradingsystem.dao.entity.RoleEntity;
import com.supremecourt.studentgradingsystem.dao.entity.RolesClaimsEntity;
import com.supremecourt.studentgradingsystem.dao.repository.ClaimRepository;
import com.supremecourt.studentgradingsystem.dao.repository.RoleRepository;
import com.supremecourt.studentgradingsystem.dao.repository.RolesClaimsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.Transactional;

@Configuration
@RequiredArgsConstructor
public class DbConfig {

    private final RoleRepository roleRepository;
    private final ClaimRepository claimRepository;
    private final RolesClaimsRepository rolesClaimsRepository;

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
        ClaimsEntity createClaim = claimRepository.findByName("create")
                .orElseGet(() -> claimRepository.save(ClaimsEntity.builder().name("create").build()));

        ClaimsEntity updateClaim = claimRepository.findByName("update")
                .orElseGet(() -> claimRepository.save(ClaimsEntity.builder().name("update").build()));

        ClaimsEntity deleteClaim = claimRepository.findByName("delete")
                .orElseGet(() -> claimRepository.save(ClaimsEntity.builder().name("delete").build()));

        // 3) Role-Claim mapping (ADMIN -> create, update, delete)
        ensureRoleClaim(adminRole, createClaim);
        ensureRoleClaim(adminRole, updateClaim);
        ensureRoleClaim(adminRole, deleteClaim);
    }

    private void ensureRoleClaim(RoleEntity role, ClaimsEntity claim) {
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