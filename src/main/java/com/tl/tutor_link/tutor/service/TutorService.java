package com.tl.tutor_link.tutor.service;

import com.tl.tutor_link.auth.service.EmailService;
import com.tl.tutor_link.common.exception.ConflictException;
import com.tl.tutor_link.common.exception.EmailSendException;
import com.tl.tutor_link.common.exception.ResourceNotFoundException;
import com.tl.tutor_link.image.service.ImageUploadService;
import com.tl.tutor_link.notification.service.NotificationService;
import com.tl.tutor_link.review.service.ReviewService;
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
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.HtmlUtils;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Tutor profile lifecycle and discovery. Owns creating and updating tutor
 * profiles, searching with dynamic filters and distance, image management,
 * and routing student enquiries to the tutor's email.
 */
@Service
public class TutorService {

    private static final Logger log = LoggerFactory.getLogger(TutorService.class);

    private final TutorRepository tutorRepository;
    private final TutorMapper tutorMapper;
    private final CourseRepository courseRepository;
    private final ImageUploadService imageUploadService;
    private final NotificationService notificationService;

    public TutorService(TutorRepository tutorRepository,
                        TutorMapper tutorMapper,
                        CourseRepository courseRepository,
                        ImageUploadService imageUploadService,
                        NotificationService notificationService
                        ) {
        this.tutorRepository = tutorRepository;
        this.tutorMapper = tutorMapper;
        this.courseRepository = courseRepository;
        this.imageUploadService = imageUploadService;
        this.notificationService = notificationService;
    }

    // ----------------------------------------------------------------------------------------------------------------
    // Profile lifecycle
    // ----------------------------------------------------------------------------------------------------------------

    @Transactional
    public TutorProfileDto createTutorProfile(User user, TutorProfileRequestDto dto) {
        log.info("User {} creating tutor profile", user.getId());

        if (tutorRepository.findByUser(user).isPresent()) {
            log.warn("User {} attempted to create duplicate tutor profile", user.getId());
            throw new ConflictException("Tutor profile already exists for this user");
        }

        Tutor tutor = new Tutor();
        tutor.setUser(user);
        tutor.setSlug(generateUniqueSlug(user));
        applyProfileUpdates(tutor, dto);

        Tutor savedTutor = tutorRepository.save(tutor);
        log.info("Tutor profile {} created for user {}", savedTutor.getId(), user.getId());
        return tutorMapper.toDto(savedTutor);
    }

    @Transactional
    public TutorProfileDto updateTutorProfile(User user, TutorProfileRequestDto dto) {
        log.info("User {} updating tutor profile", user.getId());

        Tutor tutor = findByUserOrThrow(user);
        applyProfileUpdates(tutor, dto);

        Tutor saved = tutorRepository.save(tutor);
        log.info("Tutor profile {} updated", saved.getId());
        return tutorMapper.toDto(saved);
    }

    @Transactional

    public Map<String, String> replaceProfileImage(User user, MultipartFile file) throws IOException {
        log.info("User {} replacing profile image", user.getId());

        Tutor tutor = findByUserOrThrow(user);
        String oldKey = tutor.getProfileImageKey();
        String newKey = imageUploadService.uploadProfileImage(file, user.getId());

        tutor.setProfileImageKey(newKey);
        tutorRepository.save(tutor);

        if (oldKey != null && !oldKey.isBlank()) {
            imageUploadService.deleteImage(oldKey);
        }

        return Map.of(
                "imageKey", newKey,
                "imageUrl", imageUploadService.getPresignedUrl(newKey)
        );
    }


    // ----------------------------------------------------------------------------------------------------------------
    // Profile reads
    // ----------------------------------------------------------------------------------------------------------------

    @Transactional(readOnly = true)
    public TutorProfileDto getMyTutorProfile(User user) {
        log.debug("Fetching tutor profile for user {}", user.getId());
        return tutorMapper.toDto(findByUserOrThrow(user));
    }

    @Transactional(readOnly = true)
    public TutorProfileDto getTutorById(Long id) {
        log.debug("Fetching tutor by id: {}", id);

        Tutor tutor = findByIdOrThrow(id);

        return tutorMapper.toDto(tutor);
    }

    @Transactional(readOnly = true)
    public Page<TutorProfileDto> searchTutors(TutorSearchRequestDto request, Pageable pageable) {
        log.debug("Tutor search: courseCode={}, faculty={}, location={}, remote={}, page={}, size={}",
                request.getCourseCode(), request.getFaculty(), request.getLocation(),
                request.getRemote(), pageable.getPageNumber(), pageable.getPageSize());

       Specification<Tutor> spec = buildSearchSpecification(request);
       Page<Tutor> tutors = tutorRepository.findAll(spec, pageable);

       log.debug("Tutor search returned {} of {} total results",
               tutors.getNumberOfElements(), tutors.getTotalElements());
       return tutors.map(tutorMapper::toDto);
    }

    @Transactional(readOnly = true)
    public TutorProfileDto getTutorBySlug(String slug) {
        log.debug("Fetching tutor by slug: {}", slug);
        Tutor tutor = tutorRepository.findBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Tutor not found"));
        return tutorMapper.toDto(tutor);
    }
    // ----------------------------------------------------------------------------------------------------------------
    // Enquiries -
    // ----------------------------------------------------------------------------------------------------------------

    @Transactional
    public void handleEnquiry(Long id, EnquiryRequestDto dto, User student) {
        log.info("User {} sending enquiry to tutor {}", student.getId(), id);

        Tutor tutor = findByIdOrThrow(id);

        String subject = "New TutorLink enquiry from " + fullName(student);
        String body = buildEnquiryEmailBody(dto, student);

        notificationService.send(
                tutor.getUser().getEmail(),
                subject,
                body,
                "tutor enquiry email"
        );

    }

    // ----------------------------------------------------------------------------------------------------------------
    // Private helpers
    // ----------------------------------------------------------------------------------------------------------------

    private Tutor findByUserOrThrow(User user) {
        return tutorRepository.findByUser(user)
                .orElseThrow(() -> new ResourceNotFoundException("Tutor profile not found"));
    }

    private Tutor findByIdOrThrow(Long id) {
        return tutorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tutor", id));
    }

    private Specification<Tutor> buildSearchSpecification(TutorSearchRequestDto request) {
        Specification<Tutor> spec = Specification
                .where(TutorSpecifications.hasCourseCode(request.getCourseCode()))
                .and(TutorSpecifications.hasFaculty(request.getFaculty()))
                .and(TutorSpecifications.locationContains(request.getLocation()))
                .and(TutorSpecifications.isRemote(request.getRemote()));

        if (shouldFilterByDistance(request)) {
            List<Long> nearbyIds = tutorRepository.findIdsWithinDistance(
                    request.getLatitude(),
                    request.getLongitude(),
                    AppConstants.MAX_SEARCH_DISTANCE_KM
            );
            spec = spec.and(TutorSpecifications.idIn(nearbyIds));
        }

        return spec;
    }

    private boolean shouldFilterByDistance(TutorSearchRequestDto request) {
        return request.getLatitude() != null
                && request.getLongitude() != null
                && !Boolean.TRUE.equals(request.getRemote());
    }

    private void applyProfileUpdates(Tutor tutor, TutorProfileRequestDto dto) {
        applyBasicFields(tutor, dto);
        applyFaculties(tutor, dto);
        applyCourses(tutor, dto);
        applyLanguages(tutor, dto);
        applyStyles(tutor, dto);
        applyCredentials(tutor, dto);
    }

    private void applyBasicFields(Tutor tutor, TutorProfileRequestDto dto) {
        tutor.setBio(dto.getBio());
        tutor.setTagline(dto.getTagline());
        tutor.setLocation(dto.getLocation());
        tutor.setRemote(dto.isRemote());
        tutor.setHourlyRate(dto.getHourlyRate());
        tutor.setLongitude(dto.getLongitude());
        tutor.setLatitude(dto.getLatitude());
    }

    private void applyFaculties(Tutor tutor, TutorProfileRequestDto dto) {
        if (dto.getFaculties() == null) return;
        tutor.getFaculties().clear();
        tutor.getFaculties().addAll(dto.getFaculties());
    }

    private void applyCourses(Tutor tutor, TutorProfileRequestDto dto) {
        if (dto.getCourseIds() == null) return;

        List<Course> courses = dto.getCourseIds().stream()
                .map(id -> courseRepository.findById(id)
                        .orElseThrow(() -> new ResourceNotFoundException("Course", id)))
                .toList();

        tutor.getCourses().clear();
        tutor.getCourses().addAll(courses);
    }

    private void applyLanguages(Tutor tutor, TutorProfileRequestDto dto) {
        if (dto.getLanguages() == null) return;

        tutor.getLanguages().clear();
        dto.getLanguages().forEach(l -> {
            TutorLanguage lang = new TutorLanguage();
            lang.setTutor(tutor);
            lang.setLanguage(l.getLanguage());
            lang.setLevel(l.getLevel());
            tutor.getLanguages().add(lang);
        });
    }

    private void applyStyles(Tutor tutor, TutorProfileRequestDto dto) {
        if (dto.getStyles() == null) return;

        tutor.getStyles().clear();
        dto.getStyles().forEach(s -> {
            TutorStyle style = new TutorStyle();
            style.setTutor(tutor);
            style.setLabel(s.getLabel());
            style.setDescription(s.getDescription());
            tutor.getStyles().add(style);
        });
    }

    private void applyCredentials(Tutor tutor, TutorProfileRequestDto dto) {
        if (dto.getCredentials() == null) return;

        tutor.getCredentials().clear();
        dto.getCredentials().forEach(c -> {
            TutorCredential cred = new TutorCredential();
            cred.setTutor(tutor);
            cred.setTitle(c.getTitle());
            cred.setInstitution(c.getInstitution());
            cred.setYear(c.getYear());
            tutor.getCredentials().add(cred);
        });
    }

    private String buildEnquiryEmailBody(EnquiryRequestDto dto, User student) {
        String studentName = HtmlUtils.htmlEscape(fullName(student));
        String studentEmail = HtmlUtils.htmlEscape(student.getEmail());
        String course = HtmlUtils.htmlEscape(dto.getCourse());
        String sessionType = HtmlUtils.htmlEscape(dto.getSessionType());
        String message = HtmlUtils.htmlEscape(
                dto.getMessage() != null ? dto.getMessage() : "No message provided"
        );

        return "<h2>New booking request</h2>"
                + "<p><strong>From:</strong> " + studentName + " (" + studentEmail + ")</p>"
                + "<p><strong>Course:</strong> " + course + "</p>"
                + "<p><strong>Session type:</strong> " + sessionType + "</p>"
                + "<p><strong>Message:</strong></p>"
                + "<p>" + message + "</p>"
                + "<hr>"
                + "<p>Reply directly to " + studentEmail + " to arrange the session.</p>";
    }
    private String generateUniqueSlug(User user) {
        String base = (user.getFirstname() + "-" + user.getLastname())
                .toLowerCase()
                .replaceAll("[^a-z0-9]+", "-")   // non-alphanumerics → hyphen
                .replaceAll("(^-|-$)", "");       // trim leading/trailing hyphens

        if (base.isBlank()) base = "tutor";

        final Set<String> RESERVED = Set.of("me", "search", "browse");

        String candidate = base;
        int suffix = 2;
        while (RESERVED.contains(candidate) || tutorRepository.existsBySlug(candidate)) {
            candidate = base + "-" + suffix;
            suffix++;
        }
        return candidate;
    }


    private String fullName(User user) {
        return user.getFirstname() + " " + user.getLastname();
    }

}