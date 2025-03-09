package com.hardware_today.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.hardware_today.entity.UserEntity;
import com.hardware_today.model.UserModel;
import com.hardware_today.repository.UserRepository;

import com.hardware_today.utils.JwtUtil;

@Service
public class UserService {
    private final UserRepository userRepository;
    
    private final JwtUtil jwtUtil;

    @Autowired
    public UserService(UserRepository userRepository, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
    }

    public ResponseEntity<String> saveUser(UserModel user) throws Exception {
        try {
            UserEntity userEntity = new UserEntity(user);
            userRepository.save(userEntity);
            return ResponseEntity.ok("User created successfully!");
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    public ResponseEntity<String> validateUser(UserModel user) throws Exception {
        Optional<UserEntity> targetUser = userRepository.findByEmail(user.getEmail());

        if (targetUser.isPresent()) {
            UserEntity existingUser = targetUser.get();
        	if (!existingUser.checkPassword(user.getPassword())) {
        		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Wrong password");
        	}
        	
        	 return ResponseEntity.ok(jwtUtil.generateToken(existingUser.getId(), existingUser.getEmail()));
        };

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("There is no user with the provided email");
    }

}

