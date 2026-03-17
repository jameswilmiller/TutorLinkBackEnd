package com.tl.tutor_link.service;
import com.tl.tutor_link.dto.TutorProfileRequestDto;
import com.tl.tutor_link.dto.TutorProfileDto;
import com.tl.tutor_link.model.Tutor;
import com.tl.tutor_link.model.User;
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


        dto.setBio(tutor.getBio());
        dto.setSubjects(tutor.getSubjects());
        dto.setLocation(tutor.getLocation());
        dto.setRemote(tutor.isRemote());
        dto.setHourlyRate(tutor.getHourlyRate());

        return dto;
    }
}
