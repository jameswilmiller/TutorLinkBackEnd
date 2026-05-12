package com.tl.tutor_link.tutor.model;

import com.tl.tutor_link.user.model.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
/**
 * A tutor profile. Each tutor is backed by exactly one user, who acquires
 * the TUTOR role when this profile is created. Owns child collections
 * (languages, styles, credentials) via cascade, and joins to courses and
 * faculties through separate tables.
 */
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

    // profile content
    private String bio;
    private String tagline;
    private String profileImageKey;

    // Location
    private String location;
    private Double latitude;
    private Double longitude;
    private boolean remote;

    // Pricing
    private Integer hourlyRate;

    // Academic scope
    @ManyToMany
    @JoinTable(
            name = "tutor_courses",
            joinColumns = @JoinColumn(name = "tutor_id"),
            inverseJoinColumns = @JoinColumn(name = "course_id")
    )
    private List<Course> courses = new ArrayList<>();

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name = "tutor_faculties",
            joinColumns = @JoinColumn(name = "tutor_id")
    )
    @Enumerated(EnumType.STRING)
    @Column(name = "faculty")
    private List<Faculty> faculties = new ArrayList<>();

    //profile detail (cascaded child entities)
    @OneToMany(mappedBy ="tutor", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TutorLanguage> languages = new ArrayList<>();

    @OneToMany(mappedBy ="tutor", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TutorStyle> styles = new ArrayList<>();

    @OneToMany(mappedBy ="tutor", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TutorCredential> credentials = new ArrayList<>();
}
