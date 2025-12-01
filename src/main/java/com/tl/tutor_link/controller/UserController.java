package com.tl.tutor_link.controller;

import com.tl.tutor_link.dto.UserProfileDto;
import com.tl.tutor_link.model.User;
import com.tl.tutor_link.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/users")
@RestController
public class    UserController {
    private final UserService userService;
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/me")
    public ResponseEntity<UserProfileDto> authenticatedUser() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();
        UserProfileDto dto = new UserProfileDto();
        dto.setId(currentUser.getId());
        dto.setUsername(currentUser.getUsername());
        dto.setEmail(currentUser.getEmail());
        dto.setRoles(currentUser.getRoles());
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/")
    public ResponseEntity<List<User>> allUsers() {
        List <User> users = userService.allUsers();
        return ResponseEntity.ok(users);

    }

    @PostMapping("/me/become-tutor")
    public ResponseEntity<UserProfileDto> becomeTutor() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();
        User updatedUser = userService.addTutorRole(currentUser);

        UserProfileDto dto = new UserProfileDto();
        dto.setId(updatedUser.getId());
        dto.setUsername(updatedUser.getUsername());
        dto.setEmail(updatedUser.getEmail());
        dto.setRoles(updatedUser.getRoles());

        return ResponseEntity.ok(dto);
    }

}
