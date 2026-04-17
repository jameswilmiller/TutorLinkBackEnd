package com.tl.tutor_link.tutor.mapper;

import com.tl.tutor_link.tutor.dto.TutorProfileDto;
import com.tl.tutor_link.tutor.model.Tutor;
import org.springframework.stereotype.Component;

/**
 * Maps Tutor entity objects to TutorProfileDto objects.
 */
@Component
public class TutorMapper {
    public TutorProfileDto toDto(Tutor tutor) {
        if (tutor == null) {
            return null;
        }
        TutorProfileDto dto = new TutorProfileDto();

        dto.setId(tutor.getId());
        dto.setUserId(tutor.getUser().getId());

        dto.setUsername(tutor.getUser().getDisplayUsername());
        dto.setFirstname(tutor.getUser().getFirstname());
        dto.setLastname(tutor.getUser().getLastname());

        dto.setBio(tutor.getBio());
        dto.setSubjects(tutor.getSubjects());
        dto.setLocation(tutor.getLocation());
        dto.setRemote(tutor.isRemote());
        dto.setHourlyRate(tutor.getHourlyRate());
        dto.setProfileImageKey(tutor.getProfileImageKey());
        dto.setLongitude(tutor.getLongitude());
        dto.setLatitude(tutor.getLatitude());
        return dto;
    }
}
