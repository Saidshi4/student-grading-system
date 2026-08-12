package com.supremecourt.studentgradingsystem.dao.repository;

import com.supremecourt.studentgradingsystem.dao.entity.ComponentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ComponentRepository extends JpaRepository<ComponentEntity,Long> {
    @Query(value = "SELECT sm.* FROM Component sm  WHERE sm.claim_id IN (:claimIds) AND sm.menu_id = :menuId", nativeQuery = true)
    List<ComponentEntity> findByClaimIdInAndMenuId(@Param("claimIds") List<Long> claimIds, @Param("menuId") Long menuId);
}
