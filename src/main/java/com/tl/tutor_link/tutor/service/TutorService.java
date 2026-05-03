package com.tl.tutor_link.tutor.service;
import com.tl.tutor_link.tutor.dto.TutorProfileRequestDto;
import com.tl.tutor_link.tutor.dto.TutorProfileDto;
import com.tl.tutor_link.tutor.dto.TutorSearchRequestDto;
import com.tl.tutor_link.tutor.mapper.TutorMapper;
import com.tl.tutor_link.tutor.model.*;
import com.tl.tutor_link.tutor.repository.CourseRepository;
import com.tl.tutor_link.user.model.User;
import com.tl.tutor_link.tutor.repository.TutorRepository;
import org.springframework.stereotype.Service;
import java.util.List;

import org.springframework.data.domain.Sort;
@Service
public class TutorService {
    private static final double MAX_DISTANCE_KM = 20.0;
    private static final double EARTH_RADIUS_KM = 6371.0;

    private final TutorRepository tutorRepository;
    private final TutorMapper tutorMapper;
    private final CourseRepository courseRepository;

    public TutorService(TutorRepository tutorRepository, TutorMapper tutorMapper, CourseRepository courseRepository) {
        this.tutorRepository = tutorRepository;
        this.tutorMapper = tutorMapper;
        this.courseRepository = courseRepository;
    }

    public TutorProfileDto createTutorProfile(User user, TutorProfileRequestDto dto) {
        if (tutorRepository.findByUser(user).isPresent()) {
            throw new RuntimeException("Tutor profile already exists");
        }

        Tutor tutor = new Tutor();
        tutor.setUser(user);
        applyProfileUpdates(tutor, dto);

        Tutor savedTutor = tutorRepository.save(tutor);
        return tutorMapper.toDto(savedTutor);
    }

    public TutorProfileDto updateTutorProfile(User user, TutorProfileRequestDto dto) {
        Tutor tutor = tutorRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Tutor profile could not be found"));

        applyProfileUpdates(tutor, dto);
        Tutor savedTutor = tutorRepository.save(tutor);
        return tutorMapper.toDto(savedTutor);
    }

    public TutorProfileDto getMyTutorProfile(User user) {

        Tutor tutor = tutorRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Tutor Profile could not be found"));

        return tutorMapper.toDto(tutor);

    }

    public List<TutorProfileDto> getTutors() {
        return tutorRepository.findAll()
                .stream()
                .map(tutorMapper::toDto)
                .toList();
    }

    public TutorProfileDto getTutorById(Long id) {

        Tutor tutor = tutorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tutor could not be found"));

        return tutorMapper.toDto(tutor);
    }


    public List<TutorProfileDto> searchTutors(TutorSearchRequestDto request) {
        Sort sort = getSort(request.getSort());

        return tutorRepository.findAll(sort)
                .stream()
                .filter(tutor -> matchesCourse(tutor, request.getCourseCode()))
                .filter(tutor -> matchesFaculty(tutor, request.getFaculty()))
                .filter(tutor -> matchesLocation(tutor, request.getLocation()))
                .filter(tutor -> matchesRemote(tutor, request.getRemote()))
                .filter(tutor -> matchesDistance(tutor, request, MAX_DISTANCE_KM))
                .map(tutorMapper::toDto)
                .toList();
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
                            .orElseThrow(() -> new RuntimeException("Course not found: " + id)))
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

        // credentials
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

    private boolean matchesRemote(Tutor tutor, Boolean remote) {
        if (remote == null) {
            return true;
        }
        return tutor.isRemote() == remote;
    }

    private boolean matchesCourse(Tutor tutor, String courseCode) {
        if (courseCode == null || courseCode.isBlank()) return true;
        return tutor.getCourses().stream()
                .anyMatch(c -> c.getCourseCode().equalsIgnoreCase(courseCode));
    }

    private boolean matchesFaculty(Tutor tutor, Faculty faculty) {
        if (faculty == null) return true;
        return tutor.getFaculties().contains(faculty);
    }
    private boolean matchesDistance(
            Tutor tutor,
            TutorSearchRequestDto request,
            double maxDistanceKm
    ) {

        if (request.getLatitude() == null || request.getLongitude() == null) {
            return true;
        }

        // if user selected remote distance doesnt matter
        if (Boolean.TRUE.equals(request.getRemote())) {
            return true;
        }
        // if user has any then show remote tutors regardless of location
        if (request.getRemote() == null && tutor.isRemote()) {
            return true;
        }

        double distance = calculateDistanceKm(
                request.getLatitude(),
                request.getLongitude(),
                tutor.getLatitude(),
                tutor.getLongitude()
        );
        return distance <= maxDistanceKm;
    }

    private boolean matchesLocation(Tutor tutor, String location) {
        if (location == null || location.isBlank()) return true;
        if (tutor.getLocation() == null) return false;
        return tutor.getLocation().toLowerCase().contains(location.toLowerCase());
    }

    private double calculateDistanceKm(
            double lat1,
            double lng1,
            double lat2,
            double lng2) {
        double dLat = Math.toRadians(lat2 - lat1);
        double dLng = Math.toRadians(lng2 - lng1);

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1))
                * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLng / 2)
                * Math.sin(dLng / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));

        return EARTH_RADIUS_KM * c;
    }

    private Sort getSort(String sort) {
        if (sort == null || sort.isBlank()) {
            return Sort.by(Sort.Direction.DESC, "id");
        }

        return switch (sort) {
            case "price_low" -> Sort.by(Sort.Direction.ASC, "hourlyRate");
            case "price_high" -> Sort.by(Sort.Direction.DESC, "hourlyRate");
            case "newest" -> Sort.by(Sort.Direction.DESC, "id");
            default -> Sort.by(Sort.Direction.DESC, "id");
        };
    }

    public void updateProfileImage(User user, String key) {
        Tutor tutor = tutorRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Tutor profile not found"));

        tutor.setProfileImageKey(key);
        tutorRepository.save(tutor);
    }
}
