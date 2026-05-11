package com.tl.tutor_link.user.mapper;

import com.tl.tutor_link.user.dto.UserProfileDto;
import com.tl.tutor_link.user.model.User;
import org.springframework.stereotype.Component;

/**
 * Maps User entities to UserProfileDto, exposing only public fields.
 * Keeps controllers focused on routing and out of transformation logic.
 */
@Component
public class UserMapper {
    public UserProfileDto toDto(User user) {
        UserProfileDto dto = new UserProfileDto();
        dto.setId(user.getId());
        dto.setFirstname(user.getFirstname());
        dto.setLastname(user.getLastname());
        dto.setUsername(user.getDisplayUsername());
        dto.setEmail(user.getEmail());
        dto.setRoles(user.getRoles());
        return dto;
    }
}
