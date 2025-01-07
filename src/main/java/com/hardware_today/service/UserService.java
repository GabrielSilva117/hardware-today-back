package com.hardware_today.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hardware_today.entity.UserEntity;
import com.hardware_today.model.UserModel;
import com.hardware_today.repository.UserRepository;

@Service
public class UserService {
    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }


    public UserModel saveUser(UserModel user) throws Exception {
        try {
            UserEntity userEntity = new UserEntity(user);
            userRepository.save(userEntity);
            return user;
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    public String validateUser(UserModel user) throws Exception {
        Optional<UserEntity> targetUser = userRepository.findByEmail(user.getEmail());

        if (targetUser.isEmpty()) {
            return "There is no user with the email " + user.getEmail();
        } else if (!targetUser.get().getPassword().equals(user.getPassword())) {
            return "Wrong password";
        }

        return "Sign in successful";
    }

}

