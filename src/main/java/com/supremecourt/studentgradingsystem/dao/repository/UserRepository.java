package com.supremecourt.studentgradingsystem.dao.repository;

import com.supremecourt.studentgradingsystem.dao.entity.RoleEntity;
import com.supremecourt.studentgradingsystem.dao.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity,  Long> {
    Optional<UserEntity> findByPhoneNumber(String phoneNumber);
    Optional<UserEntity> findByEmail(String email);
    Optional<UserEntity> findByUsername(String username);

    List<UserEntity> findByRole(RoleEntity role);

    @Query("SELECT a.username FROM UserEntity AS a WHERE a.username LIKE :baseUsername% ORDER BY a.username DESC limit 1")
    String findTopByUsernameLikeOrderByUsernameDesc(@Param("baseUsername") String baseUsername);}