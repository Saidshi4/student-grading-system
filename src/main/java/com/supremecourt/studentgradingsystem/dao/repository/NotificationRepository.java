package com.supremecourt.studentgradingsystem.dao.repository;

import com.supremecourt.studentgradingsystem.dao.entity.NotificationEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface NotificationRepository extends JpaRepository<NotificationEntity, Long> {

    Page<NotificationEntity> findByUserEntityId(Long userId, Pageable pageable);

    long countByUserEntityIdAndIsReadFalse(Long userId);

    Optional<NotificationEntity> findByIdAndUserEntityId(Long id, Long userId);

    @Modifying
    @Query("UPDATE notifications n SET n.isRead = true WHERE n.userEntity.id = :userId AND n.isRead = false")
    int markAllAsReadByUserId(@Param("userId") Long userId);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("DELETE FROM notifications n WHERE n.userEntity.id = :userId")
    int deleteByUserEntityId(@Param("userId") Long userId);
}
