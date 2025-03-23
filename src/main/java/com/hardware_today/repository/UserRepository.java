package com.hardware_today.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.hardware_today.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    @Query("SELECT user FROM users user WHERE user.email = :email")
    Optional<User> findByEmail(String email);
}
