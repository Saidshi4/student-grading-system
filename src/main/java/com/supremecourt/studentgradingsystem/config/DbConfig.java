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

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class DbConfig {

    private static final List<String> ALL_CLAIMS = List.of(
            "create", "update", "delete",
            "role_create", "role_read", "role_update", "role_delete",
            "role_claim_read_matrix", "role_claim_update", "role_claim_update_matrix",
            "claim_create", "menu_create",
            "create_user", "user_read", "user_update", "user_delete",
            "student_create", "student_update", "student_delete",
            "teacher_create", "teacher_update", "teacher_delete",
            "group_create", "group_update", "group_delete",
            "subject_create", "subject_update", "subject_delete",
            "semester_create", "semester_update", "semester_delete",
            "course_offering_create", "course_offering_update", "course_offering_delete",
            "enrollment_create", "enrollment_update", "enrollment_delete",
            "grade_create", "grade_update", "grade_delete",
            "class_schedule_create", "class_schedule_update", "class_schedule_delete"
    );

    private static final List<String> TEACHER_CLAIMS = List.of(
            "grade_create", "grade_update", "grade_delete"
    );

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
        RoleEntity adminRole = ensureRole("ADMIN");
        ensureRole("USER");
        RoleEntity teacherRole = ensureRole("TEACHER");
        ensureRole("STUDENT");

        for (String claimName : ALL_CLAIMS) {
            ensureRoleClaim(adminRole, ensureClaim(claimName));
        }
        for (String claimName : TEACHER_CLAIMS) {
            ensureRoleClaim(teacherRole, ensureClaim(claimName));
        }

        if (userRepository.count() == 0) {
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

    private RoleEntity ensureRole(String name) {
        return roleRepository.findByName(name)
                .orElseGet(() -> roleRepository.save(RoleEntity.builder().name(name).build()));
    }

    private ClaimEntity ensureClaim(String name) {
        return claimRepository.findByName(name)
                .orElseGet(() -> claimRepository.save(ClaimEntity.builder().name(name).build()));
    }

    private void ensureRoleClaim(RoleEntity role, ClaimEntity claim) {
        if (!rolesClaimsRepository.existsByRoleAndClaim(role, claim)) {
            rolesClaimsRepository.save(
                    RolesClaimsEntity.builder()
                            .role(role)
                            .claim(claim)
                            .build()
            );
        }
    }
}
