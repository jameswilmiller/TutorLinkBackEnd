package com.tl.tutor_link.tutor.service;

import com.tl.tutor_link.auth.service.EmailService;
import com.tl.tutor_link.common.exception.ConflictException;
import com.tl.tutor_link.common.exception.EmailSendException;
import com.tl.tutor_link.common.exception.ResourceNotFoundException;
import com.tl.tutor_link.image.service.ImageUploadService;
import com.tl.tutor_link.support.TutorFixtures;
import com.tl.tutor_link.support.UserFixtures;
import com.tl.tutor_link.tutor.dto.EnquiryRequestDto;
import com.tl.tutor_link.tutor.dto.TutorProfileDto;
import com.tl.tutor_link.tutor.dto.TutorSearchRequestDto;
import com.tl.tutor_link.tutor.mapper.TutorMapper;
import com.tl.tutor_link.tutor.model.Tutor;
import com.tl.tutor_link.tutor.repository.CourseRepository;
import com.tl.tutor_link.tutor.repository.TutorRepository;
import com.tl.tutor_link.user.model.User;
import jakarta.mail.MessagingException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TutorServiceTest {

    @Mock TutorRepository tutorRepository;
    @Mock TutorMapper tutorMapper;
    @Mock CourseRepository courseRepository;
    @Mock EmailService emailService;
    @Mock ImageUploadService imageUploadService;

    @InjectMocks TutorService tutorService;

    User user = UserFixtures.aUser().build();

    // -----------------------------------------------------------------
    // createTutorProfile
    // -----------------------------------------------------------------

    @Test
    void create_throwsWhenProfileExists() {
        when(tutorRepository.findByUser(user)).thenReturn(Optional.of(new Tutor()));

        assertThatThrownBy(() -> tutorService.createTutorProfile(user, TutorFixtures.aTutorRequest()))
                .isInstanceOf(ConflictException.class);
    }

    @Test
    void create_savesNewProfile() {
        Tutor saved = TutorFixtures.aTutor(user);
        when(tutorRepository.findByUser(user)).thenReturn(Optional.empty());
        when(tutorRepository.save(any(Tutor.class))).thenReturn(saved);
        when(tutorMapper.toDto(saved)).thenReturn(new TutorProfileDto());

        tutorService.createTutorProfile(user, TutorFixtures.aTutorRequest());

        verify(tutorRepository).save(any(Tutor.class));
    }

    // -----------------------------------------------------------------
    // updateTutorProfile
    // -----------------------------------------------------------------

    @Test
    void update_throwsWhenProfileMissing() {
        when(tutorRepository.findByUser(user)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> tutorService.updateTutorProfile(user, TutorFixtures.aTutorRequest()))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    // -----------------------------------------------------------------
    // getTutorById
    // -----------------------------------------------------------------

    @Test
    void getById_throwsWhenMissing() {
        when(tutorRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> tutorService.getTutorById(999L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    // -----------------------------------------------------------------
    // searchTutors
    // -----------------------------------------------------------------

    @Test
    void search_skipsDistanceQueryWhenRemote() {
        TutorSearchRequestDto request = new TutorSearchRequestDto();
        request.setLatitude(-27.4698);
        request.setLongitude(153.0251);
        request.setRemote(true);

        when(tutorRepository.findAll(any(Specification.class), any(PageRequest.class)))
                .thenReturn(new PageImpl<>(List.of()));

        tutorService.searchTutors(request, PageRequest.of(0, 20));

        verify(tutorRepository, never()).findIdsWithinDistance(anyDouble(), anyDouble(), anyDouble());
    }

    @Test
    void search_appliesDistanceFilterWhenCoordinatesPresent() {
        TutorSearchRequestDto request = new TutorSearchRequestDto();
        request.setLatitude(-27.4698);
        request.setLongitude(153.0251);

        when(tutorRepository.findIdsWithinDistance(anyDouble(), anyDouble(), anyDouble()))
                .thenReturn(List.of(1L, 2L));
        when(tutorRepository.findAll(any(Specification.class), any(PageRequest.class)))
                .thenReturn(new PageImpl<>(List.of()));

        tutorService.searchTutors(request, PageRequest.of(0, 20));

        verify(tutorRepository).findIdsWithinDistance(-27.4698, 153.0251, 20.0);
    }

    // -----------------------------------------------------------------
    // handleEnquiry
    // -----------------------------------------------------------------

    @Test
    void enquiry_sendsEmail() throws MessagingException {
        User tutorUser = UserFixtures.aTutor().withEmail("tutor@test.com").build();
        when(tutorRepository.findById(1L)).thenReturn(Optional.of(TutorFixtures.aTutor(tutorUser)));

        tutorService.handleEnquiry(1L, anEnquiry(), user);

        verify(emailService).sendHtmlEmail(eq("tutor@test.com"), anyString(), anyString());
    }

    @Test
    void enquiry_throwsWhenTutorMissing() {
        when(tutorRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> tutorService.handleEnquiry(999L, anEnquiry(), user))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void enquiry_throwsWhenEmailFails() throws MessagingException {
        User tutorUser = UserFixtures.aTutor().build();
        when(tutorRepository.findById(1L)).thenReturn(Optional.of(TutorFixtures.aTutor(tutorUser)));
        doThrow(new MessagingException("oops"))
                .when(emailService).sendHtmlEmail(any(), any(), any());

        assertThatThrownBy(() -> tutorService.handleEnquiry(1L, anEnquiry(), user))
                .isInstanceOf(EmailSendException.class);
    }

    // -----------------------------------------------------------------
    // Helpers
    // -----------------------------------------------------------------

    private EnquiryRequestDto anEnquiry() {
        EnquiryRequestDto dto = new EnquiryRequestDto();
        dto.setCourse("MATH1051");
        dto.setSessionType("online");
        dto.setMessage("Hi, I need help");
        return dto;
    }
}