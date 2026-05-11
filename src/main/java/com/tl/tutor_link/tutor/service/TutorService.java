package com.tl.tutor_link.tutor.service;

import com.tl.tutor_link.auth.service.EmailService;
import com.tl.tutor_link.common.exception.ConflictException;
import com.tl.tutor_link.common.exception.EmailSendException;
import com.tl.tutor_link.common.exception.ResourceNotFoundException;
import com.tl.tutor_link.tutor.dto.EnquiryRequestDto;
import com.tl.tutor_link.tutor.dto.TutorProfileRequestDto;
import com.tl.tutor_link.tutor.dto.TutorProfileDto;
import com.tl.tutor_link.tutor.dto.TutorSearchRequestDto;
import com.tl.tutor_link.tutor.mapper.TutorMapper;
import com.tl.tutor_link.tutor.model.*;
import com.tl.tutor_link.tutor.repository.CourseRepository;
import com.tl.tutor_link.tutor.repository.TutorRepository;
import com.tl.tutor_link.tutor.repository.TutorSpecifications;
import com.tl.tutor_link.user.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.tl.tutor_link.common.config.AppConstants;
import java.util.List;

@Service
public class TutorService {

    private static final Logger log = LoggerFactory.getLogger(TutorService.class);

    private final TutorRepository tutorRepository;
    private final TutorMapper tutorMapper;
    private final CourseRepository courseRepository;
    private final EmailService emailService;

    public TutorService(TutorRepository tutorRepository, TutorMapper tutorMapper, CourseRepository courseRepository, EmailService emailService) {
        this.tutorRepository = tutorRepository;
        this.tutorMapper = tutorMapper;
        this.courseRepository = courseRepository;
        this.emailService = emailService;
    }

    @Transactional
    public TutorProfileDto createTutorProfile(User user, TutorProfileRequestDto dto) {
        log.info("User {} creating tutor profile", user.getId());

        if (tutorRepository.findByUser(user).isPresent()) {
            log.warn("User {} attempted to create duplicate tutor profile", user.getId());
            throw new ConflictException("Tutor profile already exists for this user");
        }

        Tutor tutor = new Tutor();
        tutor.setUser(user);
        applyProfileUpdates(tutor, dto);

        Tutor savedTutor = tutorRepository.save(tutor);
        log.info("Tutor profile {} created for user {}", savedTutor.getId(), user.getId());
        return tutorMapper.toDto(savedTutor);
    }
    @Transactional
    public TutorProfileDto updateTutorProfile(User user, TutorProfileRequestDto dto) {
        log.info("User {} updating tutor profile", user.getId());

        Tutor tutor = tutorRepository.findByUser(user)
                .orElseThrow(() -> new ResourceNotFoundException("Tutor profile not found"));

        applyProfileUpdates(tutor, dto);
        Tutor savedTutor = tutorRepository.save(tutor);
        log.info("Tutor profile {} updated", savedTutor.getId());
        return tutorMapper.toDto(savedTutor);
    }
    @Transactional(readOnly = true)
    public TutorProfileDto getMyTutorProfile(User user) {
        log.debug("Fetching tutor profile for user {}", user.getId());

        Tutor tutor = tutorRepository.findByUser(user)
                .orElseThrow(() -> new ResourceNotFoundException("Tutor profile not found"));

        return tutorMapper.toDto(tutor);
    }
    @Transactional(readOnly = true)
    public TutorProfileDto getTutorById(Long id) {
        log.debug("Fetching tutor by id: {}", id);

        Tutor tutor = tutorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tutor", id));

        return tutorMapper.toDto(tutor);
    }
    @Transactional(readOnly = true)
    public Page<TutorProfileDto> searchTutors(TutorSearchRequestDto request, Pageable pageable) {
        log.debug("Tutor search: courseCode={}, faculty={}, location={}, remote={}, page={}, size={}",
                request.getCourseCode(), request.getFaculty(), request.getLocation(),
                request.getRemote(), pageable.getPageNumber(), pageable.getPageSize());

        Specification<Tutor> spec = Specification
                .where(TutorSpecifications.hasCourseCode(request.getCourseCode()))
                .and(TutorSpecifications.hasFaculty(request.getFaculty()))
                .and(TutorSpecifications.locationContains(request.getLocation()))
                .and(TutorSpecifications.isRemote(request.getRemote()));

        // Distance filtering uses a native query (Haversine formula) for the IDs,
        // then combines with the other specifications via an IN clause.
        if (request.getLatitude() != null && request.getLongitude() != null
                && !Boolean.TRUE.equals(request.getRemote())) {
            List<Long> nearbyIds = tutorRepository.findIdsWithinDistance(
                    request.getLatitude(),
                    request.getLongitude(),
                    AppConstants.MAX_SEARCH_DISTANCE_KM
            );
            spec = spec.and(TutorSpecifications.idIn(nearbyIds));
        }

        Page<Tutor> tutors = tutorRepository.findAll(spec, pageable);
        log.debug("Tutor search returned {} of {} total results",
                tutors.getNumberOfElements(), tutors.getTotalElements());
        return tutors.map(tutorMapper::toDto);
    }
    @Transactional
    public void updateProfileImage(User user, String key) {
        log.info("Updating profile image for user {}: {}", user.getId(), key);

        Tutor tutor = tutorRepository.findByUser(user)
                .orElseThrow(() -> new ResourceNotFoundException("Tutor profile not found"));

        tutor.setProfileImageKey(key);
        tutorRepository.save(tutor);
    }
    @Transactional
    public void handleEnquiry(Long tutorId, EnquiryRequestDto dto, User student) {
        log.info("User {} sending enquiry to tutor {}", student.getId(), tutorId);

        Tutor tutor = tutorRepository.findById(tutorId)
                .orElseThrow(() -> new ResourceNotFoundException("Tutor", tutorId));

        String studentName = student.getFirstname() + " " + student.getLastname();
        String studentEmail = student.getEmail();
        String tutorEmail = tutor.getUser().getEmail();
        String subject = "New TutorLink enquiry from " + studentName;

        String body = "<h2>New booking request</h2>"
                + "<p><strong>From:</strong> " + studentName + " (" + studentEmail + ")</p>"
                + "<p><strong>Course:</strong> " + dto.getCourse() + "</p>"
                + "<p><strong>Session type:</strong> " + dto.getSessionType() + "</p>"
                + "<p><strong>Message:</strong></p>"
                + "<p>" + (dto.getMessage() != null ? dto.getMessage() : "No message provided") + "</p>"
                + "<hr>"
                + "<p>Reply directly to " + studentEmail + " to arrange the session.</p>";

        try {
            emailService.sendVerificationEmail(tutorEmail, subject, body);
            log.info("Enquiry email sent from user {} to tutor {}", student.getId(), tutorId);
        } catch (Exception e) {
            log.error("Failed to send enquiry email from user {} to tutor {}", student.getId(), tutorId, e);
            throw new EmailSendException("Failed to send enquiry email");
        }
    }

    private void applyProfileUpdates(Tutor tutor, TutorProfileRequestDto dto) {
        tutor.setBio(dto.getBio());
        tutor.setTagline(dto.getTagline());
        tutor.setLocation(dto.getLocation());
        tutor.setRemote(dto.isRemote());
        tutor.setHourlyRate(dto.getHourlyRate());
        tutor.setProfileImageKey(dto.getProfileImageKey());
        tutor.setLongitude(dto.getLongitude());
        tutor.setLatitude(dto.getLatitude());

        tutor.getLanguages().clear();

        if (dto.getFaculties() != null) {
            tutor.getFaculties().clear();
            tutor.getFaculties().addAll(dto.getFaculties());
        }

        if (dto.getCourseIds() != null) {
            List<Course> courses = dto.getCourseIds().stream()
                    .map(id -> courseRepository.findById(id)
                            .orElseThrow(() -> new ResourceNotFoundException("Course", id)))
                    .toList();
            tutor.getCourses().clear();
            tutor.getCourses().addAll(courses);
        }

        if (dto.getLanguages() != null) {
            dto.getLanguages().forEach(l -> {
                TutorLanguage lang = new TutorLanguage();
                lang.setTutor(tutor);
                lang.setLanguage(l.getLanguage());
                lang.setLevel(l.getLevel());
                tutor.getLanguages().add(lang);
            });
        }

        tutor.getStyles().clear();
        if (dto.getStyles() != null) {
            dto.getStyles().forEach(s -> {
                TutorStyle style = new TutorStyle();
                style.setTutor(tutor);
                style.setLabel(s.getLabel());
                style.setDescription(s.getDescription());
                tutor.getStyles().add(style);
            });
        }

        tutor.getCredentials().clear();
        if (dto.getCredentials() != null) {
            dto.getCredentials().forEach(c -> {
                TutorCredential cred = new TutorCredential();
                cred.setTutor(tutor);
                cred.setTitle(c.getTitle());
                cred.setInstitution(c.getInstitution());
                cred.setYear(c.getYear());
                tutor.getCredentials().add(cred);
            });
        }
    }
}