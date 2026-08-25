package com.supremecourt.studentgradingsystem.dao.repository;

import com.supremecourt.studentgradingsystem.dao.entity.GroupEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GroupRepository extends JpaRepository<GroupEntity, Long> {
}
