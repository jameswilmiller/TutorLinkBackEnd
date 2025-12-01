package com.tl.tutor_link.service;

import com.tl.tutor_link.model.Role;
import com.tl.tutor_link.model.User;
import com.tl.tutor_link.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserService {
    private final UserRepository userRepository;
    public UserService(UserRepository userRepository, EmailService emailService) {
        this.userRepository = userRepository;
    }

    public List<User> allUsers() {
        List<User> users = new ArrayList<>();
        userRepository.findAll().forEach(users::add);
        return users;
    }

    public User addTutorRole(User user) {
        if (!user.getRoles().contains(Role.TUTOR)) {
            user.getRoles().add(Role.TUTOR);
            return userRepository.save(user);
        }
        return user;
    }



}
