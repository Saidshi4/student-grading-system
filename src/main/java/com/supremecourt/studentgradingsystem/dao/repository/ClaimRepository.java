package com.supremecourt.studentgradingsystem.dao.repository;

import com.supremecourt.studentgradingsystem.dao.entity.ClaimEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ClaimRepository extends JpaRepository<ClaimEntity,Long> {
    @Query(value = "SELECT c.name AS claimName " +
            "FROM claims c " +
            "JOIN roles_claims rc ON rc.claims_id = c.id " +
            "JOIN roles r ON rc.roles_id = r.id " +
            "WHERE r.name = :roleName", nativeQuery = true)
    List<String> findClaimNamesByRoleName(@Param("roleName") String roleName);
    @Query(value = "SELECT c.id AS id " +
            "FROM claims c " +
            "JOIN roles_claims rc ON rc.claims_id = c.id " +
            "JOIN roles r ON rc.roles_id = r.id " +
            "WHERE r.name = :roleName", nativeQuery = true)
    List<Long> findClaimIdsByRoleName(@Param("roleName") String roleName);

    Optional<ClaimEntity> findByName(String name);
}
