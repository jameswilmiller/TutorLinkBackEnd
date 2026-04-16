package com.tl.tutor_link.user.controller;

import com.tl.tutor_link.user.dto.UserProfileDto;
import com.tl.tutor_link.user.model.User;
import com.tl.tutor_link.user.service.UserService;
import com.tl.tutor_link.user.mapper.UserMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Handles user related endpoints such as retrieving the current user,
 * listing users, and updating roles
 */
@RequestMapping("/users")
@RestController
public class UserController {
    private final UserService userService;
    private final UserMapper userMapper;
    public UserController(UserService userService, UserMapper userMapper) {
        this.userService = userService;
        this.userMapper = userMapper;
    }

    /**
     * get the currently authenticated user
     */
    @GetMapping("/me")
    public ResponseEntity<UserProfileDto> authenticatedUser(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(userMapper.toDto(user));
    }

    /**
     * Get all users (admin use)
     */
    @GetMapping
    public ResponseEntity<List<UserProfileDto>> allUsers() {
        List <UserProfileDto> users = userService.allUsers()
                .stream()
                .map(userMapper::toDto)
                .toList();
        return ResponseEntity.ok(users);
    }

    /**
     * add the tutor role to the current user
     */
    @PostMapping("/me/become-tutor")
    public ResponseEntity<UserProfileDto> becomeTutor(
            @AuthenticationPrincipal User currentUser
    ) {
        User updatedUser = userService.addTutorRole(currentUser);
        return ResponseEntity.ok(userMapper.toDto(currentUser));
    }

}
