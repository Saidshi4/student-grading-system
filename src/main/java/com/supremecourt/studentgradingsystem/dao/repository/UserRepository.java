package com.supremecourt.studentgradingsystem.dao.repository;

import com.supremecourt.studentgradingsystem.dao.entity.RoleEntity;
import com.supremecourt.studentgradingsystem.dao.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity,  Long> {
    Optional<UserEntity> findByPhoneNumber(String phoneNumber);
    Optional<UserEntity> findByEmail(String email);

    List<UserEntity> findByRole(RoleEntity role);


    @Query(name = "SELECT *\n" +
            "FROM users\n" +
            "WHERE full_name ILIKE '%' || :q || '%'\n" +
            "ORDER BY full_name\n" +
            "LIMIT :limit OFFSET :offset;", nativeQuery = true)
    List<UserEntity> findByFullNameContainingIgnoreCase(String fullName);
}