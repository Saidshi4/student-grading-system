package com.supremecourt.studentgradingsystem.dao.repository;

import com.supremecourt.studentgradingsystem.dao.entity.DeviceTokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface DeviceTokenRepository extends JpaRepository<DeviceTokenEntity, Long> {

    Optional<DeviceTokenEntity> findByTokenAndUserEntityId(String token, Long userId);

    @Modifying
    @Query("DELETE FROM device_tokens d WHERE d.token = :token AND d.userEntity.id = :userId")
    int deleteByTokenAndUserId(@Param("token") String token, @Param("userId") Long userId);

    @Modifying
    @Query("DELETE FROM device_tokens d WHERE d.token = :token")
    int deleteByToken(@Param("token") String token);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("DELETE FROM device_tokens d WHERE d.userEntity.id = :userId")
    int deleteByUserEntityId(@Param("userId") Long userId);
}
