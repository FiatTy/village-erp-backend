package com.villageserp.villageerpbackend.repository;

import com.villageserp.villageerpbackend.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<UserEntity, UUID> {

    Optional<UserEntity> findByUsernameAndIsDeletedFalse(String username);

    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
}
