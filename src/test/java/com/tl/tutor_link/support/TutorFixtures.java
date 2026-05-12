package com.tl.tutor_link.support;

import com.tl.tutor_link.tutor.dto.TutorProfileRequestDto;
import com.tl.tutor_link.tutor.model.Tutor;
import com.tl.tutor_link.user.model.User;

/**
 * Builders for Tutor entity and TutorProfileRequestDto test data.
 */
public class TutorFixtures {

    public static Tutor aTutor(User user) {
        Tutor tutor = new Tutor();
        tutor.setId(1L);
        tutor.setUser(user);
        tutor.setBio("Default bio");
        tutor.setTagline("Default tagline");
        tutor.setLocation("Brisbane");
        tutor.setRemote(false);
        tutor.setHourlyRate(40);
        tutor.setLatitude(-27.4698);
        tutor.setLongitude(153.0251);
        return tutor;
    }

    public static TutorProfileRequestDto aTutorRequest() {
        TutorProfileRequestDto dto = new TutorProfileRequestDto();
        dto.setBio("I tutor maths");
        dto.setTagline("Patient and clear");
        dto.setLocation("Brisbane");
        dto.setRemote(false);
        dto.setHourlyRate(45);
        dto.setLatitude(-27.4698);
        dto.setLongitude(153.0251);
        return dto;
    }
}