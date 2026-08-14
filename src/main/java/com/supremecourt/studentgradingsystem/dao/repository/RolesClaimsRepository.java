package com.supremecourt.studentgradingsystem.dao.repository;

import com.supremecourt.studentgradingsystem.dao.entity.ClaimEntity;
import com.supremecourt.studentgradingsystem.dao.entity.RoleEntity;
import com.supremecourt.studentgradingsystem.dao.entity.RolesClaimsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface RolesClaimsRepository extends JpaRepository<RolesClaimsEntity,Long> {
    @Query(value = "SELECT rc.id AS id, rc.created_at AS created_at, rc.updated_at AS updated_at, rc.roles_id AS role_id, rc.claims_id AS claim_id " +
            "FROM roles_claims rc " +
            "JOIN roles r ON rc.roles_id = r.id " +
            "WHERE r.name = :roleName",
            nativeQuery = true)
    List<RolesClaimsEntity> findByRoleName(@Param("roleName") String roleName);
    @Query(value = "select * from roles_claims where roles_id=:roleId and claims_id=:claimId",nativeQuery = true)
    Optional<RolesClaimsEntity> findByRoleIdAndClaimId(@Param("roleId")Long roleId,
                                                       @Param("claimId") Long claimId);
    @Modifying
    @Transactional
    @Query(value = "delete from roles_claims where roles_id=:roleId and claims_id=:claimId",nativeQuery = true)
    void deleteByRoleIdAndClaimId(@Param("roleId")Long roleId,
                                  @Param("claimId") Long claimId);

    boolean existsByRoleAndClaim(RoleEntity role, ClaimEntity claim);

}
