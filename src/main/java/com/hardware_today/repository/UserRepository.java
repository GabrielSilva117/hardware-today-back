package com.hardware_today.repository;

import com.hardware_today.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {

    @Query("SELECT user FROM UserEntity user WHERE user.email = :email")
    Optional<UserEntity> findByEmail(String email);
}
