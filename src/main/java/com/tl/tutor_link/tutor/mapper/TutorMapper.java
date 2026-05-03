package com.tl.tutor_link.tutor.mapper;

import com.tl.tutor_link.image.service.ImageUploadService;
import com.tl.tutor_link.tutor.dto.TutorCredentialDto;
import com.tl.tutor_link.tutor.dto.TutorLanguageDto;
import com.tl.tutor_link.tutor.dto.TutorProfileDto;
import com.tl.tutor_link.tutor.dto.TutorStyleDto;
import com.tl.tutor_link.tutor.model.Tutor;
import org.springframework.stereotype.Component;

/**
 * Maps Tutor entity objects to TutorProfileDto objects.
 */
@Component
public class TutorMapper {
    private final ImageUploadService imageUploadService;
    private final CourseMapper courseMapper;

    public TutorMapper(ImageUploadService imageUploadService, CourseMapper courseMapper) {
        this.imageUploadService = imageUploadService;
        this.courseMapper = courseMapper;
    }
    public TutorProfileDto toDto(Tutor tutor) {
        if (tutor == null) return null;

        TutorProfileDto dto = new TutorProfileDto();

        dto.setId(tutor.getId());
        dto.setUserId(tutor.getUser().getId());

        dto.setUsername(tutor.getUser().getDisplayUsername());
        dto.setFirstname(tutor.getUser().getFirstname());
        dto.setLastname(tutor.getUser().getLastname());

        dto.setBio(tutor.getBio());
        dto.setTagline(tutor.getTagline());

        dto.setLocation(tutor.getLocation());
        dto.setRemote(tutor.isRemote());
        dto.setHourlyRate(tutor.getHourlyRate());
        dto.setProfileImageKey(tutor.getProfileImageKey());
        dto.setProfileImageUrl(imageUploadService.getPublicUrl(tutor.getProfileImageKey()));
        dto.setLongitude(tutor.getLongitude());
        dto.setLatitude(tutor.getLatitude());
        dto.setCourses(tutor.getCourses().stream()
                .map(courseMapper::toDto)
                .toList());
        dto.setFaculties(tutor.getFaculties());
        dto.setLanguages(tutor.getLanguages().stream()
                .map(l -> {
                    TutorLanguageDto dto2 = new TutorLanguageDto();
                    dto2.setId(l.getId());
                    dto2.setLanguage(l.getLanguage());
                    dto2.setLevel(l.getLevel());
                    return dto2;
                }).toList());
        dto.setStyles(tutor.getStyles().stream()
                .map(s -> {
                    TutorStyleDto dto2 = new TutorStyleDto();
                    dto2.setId(s.getId());
                    dto2.setLabel(s.getLabel());
                    dto2.setDescription(s.getDescription());
                    return dto2;
                }).toList());
        dto.setCredentials(tutor.getCredentials().stream()
                .map(c -> {
                    TutorCredentialDto dto2 = new TutorCredentialDto();
                    dto2.setId(c.getId());
                    dto2.setTitle(c.getTitle());
                    dto2.setInstitution(c.getInstitution());
                    dto2.setYear(c.getYear());
                    return dto2;
                }).toList());


        return dto;
    }
}
