package com.tl.tutor_link.tutor.dto;

import com.tl.tutor_link.tutor.model.Faculty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class TutorProfileRequestDto {
    @Size(max = 2000, message = "Bio must be at most 2000 characters")
    private String bio;
    @Size(max = 200, message = "Tagline must be at most 200 characters")
    private String tagline;

    private List<Long> courseIds;
    private List<Faculty> faculties;
    @Size(max = 200, message = "Location must be at most 200 characters")
    private String location;
    private boolean remote;
    @Min(value = 0, message = "Hourly rate must be 0 or greater")
    @Max(value = 1000, message = "Hourly rate must be at most 1000")
    private Integer hourlyRate;
    @Size(max = 500, message = "Profile image key is invalid")
    private String profileImageKey;
    private Double longitude;
    private Double latitude;
    @Valid
    private List<TutorLanguageRequestDto> languages;
    @Valid
    private List<TutorStyleRequestDto>  styles;
    @Valid
    private List<TutorCredentialRequestDto> credentials;
}
