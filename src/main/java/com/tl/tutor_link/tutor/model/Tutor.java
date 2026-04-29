package com.tl.tutor_link.tutor.model;

import com.tl.tutor_link.user.model.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "tutors")
@Getter
@Setter
public class Tutor {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @OneToOne
    @JoinColumn(name= "user_id", nullable = false, unique = true)
    private User user;

    @OneToMany(mappedBy ="tutor", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TutorLanguage> languages = new ArrayList<>();

    @OneToMany(mappedBy ="tutor", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TutorStyle> styles = new ArrayList<>();

    @OneToMany(mappedBy ="tutor", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TutorCredential> credentials = new ArrayList<>();

    @Column
    private String bio;
    private String tagline;
    private String subjects;
    private String location;
    private boolean remote;
    private Integer hourlyRate;
    private String profileImageKey;
    private Double latitude;
    private Double longitude;
}
