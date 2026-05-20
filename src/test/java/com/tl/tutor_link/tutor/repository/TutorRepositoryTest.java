package com.tl.tutor_link.tutor.repository;

import com.tl.tutor_link.support.RepositoryTestBase;
import com.tl.tutor_link.support.TestDataFactory;
import com.tl.tutor_link.tutor.model.Tutor;
import com.tl.tutor_link.tutor.model.TutorLanguage;
import com.tl.tutor_link.user.model.User;
import com.tl.tutor_link.user.repository.UserRepository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class TutorRepositoryTest extends RepositoryTestBase {

    @Autowired
    private TutorRepository tutorRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    void save_persistsTutorWithUserProfile() {
        // Arrange
        User savedUser = saveTutorUser();
        Tutor tutor = TestDataFactory.tutor(savedUser);

        // Act
        Tutor savedTutor = tutorRepository.save(tutor);

        // Assert
        assertThat(savedTutor.getId()).isNotNull();

        assertThat(savedTutor)
                .extracting(Tutor::getBio, Tutor::getLocation, Tutor::getHourlyRate)
                .containsExactly("Software engineering tutor", "Brisbane", 50);

        assertThat(savedTutor.getUser().getEmail())
                .isEqualTo(savedUser.getEmail());
    }

    @Test
    void findByUser_returnsTutorProfile() {
        // Arrange
        User savedUser = saveTutorUser();
        Tutor savedTutor = tutorRepository.save(TestDataFactory.tutor(savedUser));

        // Act
        Optional<Tutor> result = tutorRepository.findByUser(savedUser);

        // Assert
        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(savedTutor.getId());
    }

    @Test
    void findByUser_returnsEmptyWhenUserHasNoTutorProfile() {
        // Arrange
        User savedUser = saveTutorUser();

        // Act
        Optional<Tutor> result = tutorRepository.findByUser(savedUser);

        // Assert
        assertThat(result).isEmpty();
    }

    @Test
    void save_cascadesTutorLanguages() {
        // Arrange
        User savedUser = saveTutorUser();
        Tutor tutor = TestDataFactory.tutor(savedUser);

        TutorLanguage language = TestDataFactory.englishLanguage(tutor);
        tutor.getLanguages().add(language);

        // Act
        Tutor savedTutor = tutorRepository.save(tutor);

        // Assert
        assertThat(savedTutor.getLanguages()).hasSize(1);
        assertThat(savedTutor.getLanguages().getFirst().getLanguage())
                .isEqualTo("English");
    }

    private User saveTutorUser() {
        return userRepository.save(TestDataFactory.tutorUser());
    }
}