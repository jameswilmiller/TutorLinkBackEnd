package com.tl.tutor_link.support;

import com.tl.tutor_link.tutor.dto.TutorProfileRequestDto;
import com.tl.tutor_link.tutor.model.*;
import com.tl.tutor_link.user.model.Role;
import com.tl.tutor_link.user.model.User;

import java.util.List;

public final class TestDataFactory {

    private TestDataFactory() {
    }

    public static User studentUser() {
        User user = new User();

        user.setFirstname("James");
        user.setLastname("Miller");
        user.setUsername("james");
        user.setEmail("james@test.com");
        user.setPassword("password");
        user.setEnabled(true);

        user.getRoles().add(Role.STUDENT);

        return user;
    }

    public static User tutorUser() {
        User user = studentUser();

        user.getRoles().add(Role.TUTOR);

        return user;
    }

    public static Course csse2010Course() {
        Course course = new Course();

        course.setCourseCode("CSSE2010");
        course.setCourseName("Intro Software Engineering");
        course.setFaculty("ENGINEERING_ARCHITECTURE_IT");

        return course;
    }

    public static Tutor tutor(User user) {
        Tutor tutor = new Tutor();

        tutor.setUser(user);
        tutor.setBio("Software engineering tutor");
        tutor.setTagline("High distinction student");
        tutor.setLocation("Brisbane");
        tutor.setLatitude(-27.4975);
        tutor.setLongitude(153.0137);
        tutor.setRemote(true);
        tutor.setHourlyRate(50);

        tutor.getFaculties().add(Faculty.ENGINEERING_ARCHITECTURE_IT);

        return tutor;
    }

    public static TutorLanguage englishLanguage(Tutor tutor) {
        TutorLanguage language = new TutorLanguage();

        language.setTutor(tutor);
        language.setLanguage("English");
        language.setLevel(LanguageLevel.NATIVE);

        return language;
    }

    public static TutorProfileRequestDto tutorProfileRequest() {
        TutorProfileRequestDto dto = new TutorProfileRequestDto();

        dto.setBio("Software engineering tutor");
        dto.setTagline("Helping students understand code clearly");
        dto.setLocation("Brisbane");
        dto.setLatitude(-27.4975);
        dto.setLongitude(153.0137);
        dto.setRemote(true);
        dto.setHourlyRate(50);
        dto.setFaculties(List.of(Faculty.ENGINEERING_ARCHITECTURE_IT));

        return dto;
    }
}