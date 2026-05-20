package com.tl.tutor_link.tutor.service;

import com.tl.tutor_link.common.exception.ConflictException;
import com.tl.tutor_link.common.exception.ResourceNotFoundException;
import com.tl.tutor_link.image.service.ImageUploadService;
import com.tl.tutor_link.notification.service.NotificationService;
import com.tl.tutor_link.support.TestDataFactory;
import com.tl.tutor_link.tutor.dto.TutorProfileDto;
import com.tl.tutor_link.tutor.dto.TutorProfileRequestDto;
import com.tl.tutor_link.tutor.mapper.TutorMapper;
import com.tl.tutor_link.tutor.model.Tutor;
import com.tl.tutor_link.tutor.repository.CourseRepository;
import com.tl.tutor_link.tutor.repository.TutorRepository;
import com.tl.tutor_link.user.model.User;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import static org.mockito.ArgumentMatchers.any;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TutorServiceTest {

    @Mock
    private TutorRepository tutorRepository;

    @Mock
    private TutorMapper tutorMapper;

    @Mock
    private CourseRepository courseRepository;

    @Mock
    private ImageUploadService imageUploadService;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private TutorService tutorService;

    // ----------------------------------------------------------------------------------------------------------------
    // createTutorProfile
    // ----------------------------------------------------------------------------------------------------------------

    @Test
    void createTutorProfile_whenUserAlreadyHasTutorProfile_throwsConflictException() {

        // Arrange
        User user = TestDataFactory.tutorUser();
        Tutor existingTutor = TestDataFactory.tutor(user);

        when(tutorRepository.findByUser(user))
                .thenReturn(Optional.of(existingTutor));

        // Act + Assert
        assertThatThrownBy(() -> tutorService.createTutorProfile(user, null))
                .isInstanceOf(ConflictException.class)
                .hasMessage("Tutor profile already exists for this user");

        verify(tutorRepository, never()).save(any(Tutor.class));
    }

    @Test
    void createTutorProfile_whenUserHasNoExistingProfile_savesTutorWithRequestFields() {

        // Arrange
        User user = TestDataFactory.tutorUser();
        TutorProfileRequestDto request = TestDataFactory.tutorProfileRequest();

        Tutor savedTutor = TestDataFactory.tutor(user);
        TutorProfileDto expectedDto = new TutorProfileDto();

        when(tutorRepository.findByUser(user))
                .thenReturn(Optional.empty());

        when(tutorRepository.save(any(Tutor.class)))
                .thenReturn(savedTutor);

        when(tutorMapper.toDto(savedTutor))
                .thenReturn(expectedDto);

        // Act
        TutorProfileDto result = tutorService.createTutorProfile(user, request);

        // Assert
        ArgumentCaptor<Tutor> tutorCaptor =
                ArgumentCaptor.forClass(Tutor.class);

        verify(tutorRepository).save(tutorCaptor.capture());

        Tutor tutorToSave = tutorCaptor.getValue();

        assertThat(tutorToSave.getUser()).isEqualTo(user);
        assertThat(tutorToSave.getBio()).isEqualTo("Software engineering tutor");
        assertThat(tutorToSave.getLocation()).isEqualTo("Brisbane");
        assertThat(tutorToSave.getHourlyRate()).isEqualTo(50);
        assertThat(tutorToSave.isRemote()).isTrue();

        assertThat(result).isSameAs(expectedDto);
    }

    // ----------------------------------------------------------------------------------------------------------------
    // updateTutorProfile
    // ----------------------------------------------------------------------------------------------------------------

    @Test
    void updateTutorProfile_whenProfileExists_updatesTutorFields() {

        // Arrange
        User user = TestDataFactory.tutorUser();
        Tutor existingTutor = TestDataFactory.tutor(user);

        TutorProfileRequestDto request = TestDataFactory.tutorProfileRequest();
        request.setBio("Updated bio");
        request.setTagline("Updated tagline");
        request.setLocation("St Lucia");
        request.setHourlyRate(70);
        request.setRemote(false);

        TutorProfileDto expectedDto = new TutorProfileDto();

        when(tutorRepository.findByUser(user))
                .thenReturn(Optional.of(existingTutor));

        when(tutorRepository.save(existingTutor))
                .thenReturn(existingTutor);

        when(tutorMapper.toDto(existingTutor))
                .thenReturn(expectedDto);

        // Act
        TutorProfileDto result = tutorService.updateTutorProfile(user, request);

        // Assert
        assertThat(existingTutor.getBio()).isEqualTo("Updated bio");
        assertThat(existingTutor.getTagline()).isEqualTo("Updated tagline");
        assertThat(existingTutor.getLocation()).isEqualTo("St Lucia");
        assertThat(existingTutor.getHourlyRate()).isEqualTo(70);
        assertThat(existingTutor.isRemote()).isFalse();

        assertThat(result).isSameAs(expectedDto);

        verify(tutorRepository).save(existingTutor);
        verify(tutorMapper).toDto(existingTutor);
    }

    // ----------------------------------------------------------------------------------------------------------------
    // getMyTutorProfile
    // ----------------------------------------------------------------------------------------------------------------

    @Test
    void getMyTutorProfile_whenProfileExists_returnsDto() {

        // Arrange
        User user = TestDataFactory.tutorUser();
        Tutor tutor = TestDataFactory.tutor(user);
        TutorProfileDto expectedDto = new TutorProfileDto();

        when(tutorRepository.findByUser(user))
                .thenReturn(Optional.of(tutor));

        when(tutorMapper.toDto(tutor))
                .thenReturn(expectedDto);

        // Act
        TutorProfileDto result = tutorService.getMyTutorProfile(user);

        // Assert
        assertThat(result).isSameAs(expectedDto);

        verify(tutorMapper).toDto(tutor);
    }

    @Test
    void getMyTutorProfile_whenProfileDoesNotExist_throwsResourceNotFoundException() {

        // Arrange
        User user = TestDataFactory.tutorUser();

        when(tutorRepository.findByUser(user))
                .thenReturn(Optional.empty());

        // Act + Assert
        assertThatThrownBy(() -> tutorService.getMyTutorProfile(user))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Tutor profile not found");
    }

    // ----------------------------------------------------------------------------------------------------------------
    // getTutorById
    // ----------------------------------------------------------------------------------------------------------------

    @Test
    void getTutorById_whenTutorDoesNotExist_throwsResourceNotFoundException() {

        // Arrange
        Long tutorId = 999L;

        when(tutorRepository.findById(tutorId))
                .thenReturn(Optional.empty());

        // Act + Assert
        assertThatThrownBy(() -> tutorService.getTutorById(tutorId))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}