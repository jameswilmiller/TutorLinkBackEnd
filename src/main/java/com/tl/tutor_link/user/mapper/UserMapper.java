package com.tl.tutor_link.user.mapper;

import com.tl.tutor_link.user.dto.UserProfileDto;
import com.tl.tutor_link.user.model.User;
import org.springframework.stereotype.Component;

/**
 * Maps User entity objects to UserProfileDto objects.
 *
 * This keeps controllers clean and separates transformation logic from business logic
 */
@Component
public class UserMapper {
    public UserProfileDto toDto(User user) {
        UserProfileDto dto = new UserProfileDto();
        dto.setId(user.getId());
        dto.setFirstname(user.getFirstname());
        dto.setLastname(user.getLastname());
        dto.setEmail(user.getEmail());
        dto.setRoles(user.getRoles());
        dto.setUsername(user.getDisplayUsername());

        return dto;
    }

}
