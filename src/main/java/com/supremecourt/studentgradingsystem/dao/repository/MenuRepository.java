package com.supremecourt.studentgradingsystem.dao.repository;

import com.supremecourt.studentgradingsystem.dao.entity.MenuEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MenuRepository extends JpaRepository<MenuEntity,Long> {
    @Query(value = "SELECT * FROM menus WHERE claim_id IN (:claimIds)", nativeQuery = true)
   List<MenuEntity> findByClaimIdIn(@Param("claimIds") List<Long> claimIds);
    @Query(value = "select * from menus where name=:name",nativeQuery = true)
    Optional<MenuEntity> findByName(@Param("name") String name);
    @Query(value = "select * from menus where path=:path limit 1",nativeQuery = true)
    Optional<MenuEntity> findByPath(@Param("path") String path);

    @EntityGraph(attributePaths = {"components"})
    @Query("SELECT m FROM menus m JOIN m.claims c WHERE c.id IN :claimIds ORDER BY m.orderNumber ASC")
    List<MenuEntity> findMenusByClaimIds(@Param("claimIds") List<Long> claimIds);
}
