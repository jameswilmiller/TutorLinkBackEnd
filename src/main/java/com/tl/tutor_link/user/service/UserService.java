package com.tl.tutor_link.user.service;

import com.tl.tutor_link.user.model.Role;
import com.tl.tutor_link.user.model.User;
import com.tl.tutor_link.user.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<User> allUsers() {
        return (List<User>) userRepository.findAll();
    }

    public User addTutorRole(User user) {
        if (user.getRoles().contains(Role.TUTOR)) {
            return user;
        }
        user.getRoles().add(Role.TUTOR);
        return userRepository.save(user);
    }



}


