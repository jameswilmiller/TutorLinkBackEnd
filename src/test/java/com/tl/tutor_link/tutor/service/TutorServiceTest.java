package com.tl.tutor_link.tutor.service;

import com.tl.tutor_link.auth.service.EmailService;
import com.tl.tutor_link.common.exception.ConflictException;
import com.tl.tutor_link.common.exception.ResourceNotFoundException;
import com.tl.tutor_link.tutor.dto.TutorProfileRequestDto;
import com.tl.tutor_link.tutor.mapper.TutorMapper;
import com.tl.tutor_link.tutor.model.Tutor;
import com.tl.tutor_link.tutor.repository.CourseRepository;
import com.tl.tutor_link.tutor.repository.TutorRepository;
import com.tl.tutor_link.user.model.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("TutorService")
class TutorServiceTest {

    @Mock
    private TutorRepository tutorRepository;

    @Mock
    private TutorMapper tutorMapper;

    @Mock
    private CourseRepository courseRepository;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private TutorService tutorService;

    @Nested
    @DisplayName("createTutorProfile")
    class CreateTutorProfile {

        @Test
        @DisplayName("throws ConflictException when user already has a tutor profile")
        void throwsConflictException_whenUserAlreadyHasTutorProfile() {
            User user = userWithId(1L);
            Tutor existingTutor = new Tutor();

            when(tutorRepository.findByUser(user))
                    .thenReturn(Optional.of(existingTutor));

            assertThrows(ConflictException.class, () ->
                    tutorService.createTutorProfile(user, new TutorProfileRequestDto())
            );

            verify(tutorRepository, never()).save(any(Tutor.class));
        }
    }

    @Nested
    @DisplayName("updateTutorProfile")
    class UpdateTutorProfile {

        @Test
        @DisplayName("throws ResourceNotFoundException when user has no tutor profile")
        void throwsResourceNotFoundException_whenUserHasNoTutorProfile() {
            User user = userWithId(1L);

            when(tutorRepository.findByUser(user))
                    .thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class, () ->
                    tutorService.updateTutorProfile(user, new TutorProfileRequestDto())
            );

            verify(tutorRepository, never()).save(any(Tutor.class));
        }
    }

    @Nested
    @DisplayName("updateProfileImage")
    class UpdateProfileImage {

        @Test
        @DisplayName("updates profile image key for existing tutor profile")
        void updatesProfileImageKey_whenTutorProfileExists() {
            User user = userWithId(1L);
            Tutor tutor = new Tutor();
            tutor.setProfileImageKey("old-image-key");

            when(tutorRepository.findByUser(user))
                    .thenReturn(Optional.of(tutor));

            tutorService.updateProfileImage(user, "new-image-key");

            verify(tutorRepository).save(tutor);
            assert tutor.getProfileImageKey().equals("new-image-key");
        }
    }

    private User userWithId(Long id) {
        User user = new User();
        user.setId(id);
        return user;
    }
}