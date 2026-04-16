package com.tl.tutor_link.tutor;
import com.tl.tutor_link.dto.TutorProfileRequestDto;
import com.tl.tutor_link.dto.TutorProfileDto;
import com.tl.tutor_link.dto.TutorSearchRequestDto;
import com.tl.tutor_link.model.Tutor;
import com.tl.tutor_link.user.model.User;
import com.tl.tutor_link.repository.TutorRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TutorService {

    private final TutorRepository tutorRepository;

    public TutorService(TutorRepository tutorRepository) {
        this.tutorRepository = tutorRepository;
    }

    public TutorProfileDto createTutorProfile(User user, TutorProfileRequestDto dto) {
        if (tutorRepository.findByUser(user).isPresent()) {
            throw new RuntimeException("Tutor profile already exists");
        }

        Tutor tutor = new Tutor();
        tutor.setUser(user);
        tutor.setBio(dto.getBio());
        tutor.setSubjects(dto.getSubjects());
        tutor.setLocation(dto.getLocation());
        tutor.setRemote(dto.isRemote());
        tutor.setHourlyRate(dto.getHourlyRate());
        tutor.setProfileImageKey(dto.getProfileImageKey());
        tutor.setLongitude(dto.getLongitude());
        tutor.setLatitude(dto.getLatitude());
        Tutor savedTutor = tutorRepository.save(tutor);
        return mapToDto(savedTutor);
    }

    public TutorProfileDto updateTutorProfile(User user, TutorProfileRequestDto dto) {
        Tutor tutor = tutorRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Tutor profile could not be found"));

        tutor.setBio(dto.getBio());
        tutor.setSubjects(dto.getSubjects());
        tutor.setLocation(dto.getLocation());
        tutor.setRemote(dto.isRemote());
        tutor.setHourlyRate(dto.getHourlyRate());
        tutor.setProfileImageKey(dto.getProfileImageKey());
        tutor.setLongitude(dto.getLongitude());
        tutor.setLatitude(dto.getLatitude());
        Tutor updatedTutor = tutorRepository.save(tutor);

        return mapToDto(updatedTutor);
    }

    public TutorProfileDto getMyTutorProfile(User user) {

        Tutor tutor = tutorRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Tutor Profile could not be found"));

        return mapToDto(tutor);

    }

    public List<TutorProfileDto> getTutors() {
        return tutorRepository.findAll()
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    public TutorProfileDto getTutorById(Long id) {

        Tutor tutor = tutorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tutor could not be found"));

        return mapToDto(tutor);
    }

    private TutorProfileDto mapToDto(Tutor tutor) {
        TutorProfileDto dto = new TutorProfileDto();

        dto.setId(tutor.getId());
        dto.setUserId(tutor.getUser().getId());
        dto.setUsername(tutor.getUser().getDisplayUsername());
        dto.setFirstname(tutor.getUser().getFirstname());
        dto.setLastname(tutor.getUser().getLastname());

        dto.setBio(tutor.getBio());
        dto.setSubjects(tutor.getSubjects());
        dto.setLocation(tutor.getLocation());
        dto.setRemote(tutor.isRemote());
        dto.setHourlyRate(tutor.getHourlyRate());
        dto.setProfileImageKey(tutor.getProfileImageKey());
        dto.setLongitude(tutor.getLongitude());
        dto.setLatitude(tutor.getLatitude());
        return dto;
    }

    public List<TutorProfileDto> searchTutors(TutorSearchRequestDto request) {
        final double MAX_DIST_KM = 20.0;

        return tutorRepository.findAll().
                stream()
                .filter(tutor -> matchesSubject(tutor, request.getSubject()))
                .filter(tutor -> matchesDistance(tutor, request, MAX_DIST_KM))
                .map(this::mapToDto)
                .collect(Collectors.toList());
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
        //make sure to always include remote tutors
        if (tutor.isRemote()) return true;

        if (request.getLatitude() == null || request.getLongitude() == null) {
            return true;
        }
        if (tutor.getLatitude() == null || tutor.getLongitude() == null) {
            return false;
        }

        double distance = calculateDistanceKm(
                request.getLatitude(),
                request.getLongitude(),
                tutor.getLatitude(),
                tutor.getLongitude()
        );
        return distance <= maxDistanceKm;
    }

    private double calculateDistanceKm(
            double lat1,
            double lng1,
            double lat2,
            double lng2) {
        final double EARTH_RADIUS = 6731.0;

        double dLat = Math.toRadians(lat2 - lat1);
        double dLng = Math.toRadians(lng2 - lng1);

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1))
                * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLng / 2)
                * Math.sin(dLng / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));

        return EARTH_RADIUS * c;


    }
}
