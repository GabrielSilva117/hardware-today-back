package com.hardware_today.service;

import com.hardware_today.entity.UserEntity;
import com.hardware_today.model.UserModel;
import com.hardware_today.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

}

