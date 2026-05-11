package com.tl.tutor_link.user.service;

import com.tl.tutor_link.user.model.Role;
import com.tl.tutor_link.user.model.User;
import com.tl.tutor_link.user.repository.UserRepository;

import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

/**
 * User-level operations distinct from authentication. Promotes a student
 * to a tutor and exposes user listings for administrative use.
 */
@Service
public class UserService {

    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    @Transactional(readOnly = true)
    public List<User> allUsers() {
        return (List<User>) userRepository.findAll();
    }

    @Transactional
    public User addTutorRole(User user) {
        if (user.getRoles().contains(Role.TUTOR)) {
            return user;
        }
        log.info("Adding TUTOR role to user {}", user.getId());
        user.getRoles().add(Role.TUTOR);
        return userRepository.save(user);
    }



}


