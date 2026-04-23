package com.tl.tutor_link.tutor.service;
import com.tl.tutor_link.tutor.dto.TutorProfileRequestDto;
import com.tl.tutor_link.tutor.dto.TutorProfileDto;
import com.tl.tutor_link.tutor.dto.TutorSearchRequestDto;
import com.tl.tutor_link.tutor.mapper.TutorMapper;
import com.tl.tutor_link.tutor.model.Tutor;
import com.tl.tutor_link.user.model.User;
import com.tl.tutor_link.tutor.repository.TutorRepository;
import org.springframework.stereotype.Service;
import java.util.List;


@Service
public class TutorService {
    private static final double MAX_DISTANCE_KM = 20.0;
    private static final double EARTH_RADIUS_KM = 6371.0;

    private final TutorRepository tutorRepository;
    private final TutorMapper tutorMapper;
    public TutorService(TutorRepository tutorRepository, TutorMapper tutorMapper) {
        this.tutorRepository = tutorRepository;
        this.tutorMapper = tutorMapper;
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
        return tutorRepository.findAll()
                .stream()
                .filter(tutor -> matchesSubject(tutor, request.getSubject()))
                .filter(tutor -> matchesLocation(tutor, request.getLocation()))
                .filter(tutor -> matchesRemote(tutor, request.getRemote()))
                .filter(tutor -> matchesDistance(tutor, request, MAX_DISTANCE_KM))
                .map(tutorMapper::toDto)
                .toList();
    }

    private void applyProfileUpdates(Tutor tutor, TutorProfileRequestDto dto) {
        tutor.setBio(dto.getBio());
        tutor.setSubjects(dto.getSubjects());
        tutor.setLocation(dto.getLocation());
        tutor.setRemote(dto.isRemote());
        tutor.setHourlyRate(dto.getHourlyRate());
        tutor.setProfileImageKey(dto.getProfileImageKey());
        tutor.setLongitude(dto.getLongitude());
        tutor.setLatitude(dto.getLatitude());
    }

    private boolean matchesRemote(Tutor tutor, Boolean remote) {
        if (remote == null) {
            return true;
        }
        return tutor.isRemote() == remote;
    }
    private boolean matchesSubject(Tutor tutor, String subject) {
        if (subject == null || subject.isBlank()) return true;
        if (tutor.getSubjects() == null) return false;
        return tutor.getSubjects().toLowerCase().contains(subject.toLowerCase());
    }

    private boolean matchesDistance(
            Tutor tutor,
            TutorSearchRequestDto request,
            double maxDistanceKm
    ) {

        if (tutor.isRemote()) return true;
        if (request.getLatitude() == null || request.getLongitude() == null) return true;
        if (tutor.getLatitude() == null || tutor.getLongitude() == null) return false;

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
}
