package com.example.userservice.dao.repository;

import com.example.userservice.dao.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;
@Repository
public interface UserRepository extends JpaRepository<UserEntity, UUID> {
    Optional<UserEntity> findByFullName(String fullName);

    Optional<UserEntity> findByPhoneNumber(String phoneNumber);
}
