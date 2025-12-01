package com.tl.tutor_link.model;

import jakarta.persistence.*;

@Entity
@Table(name = "tutors")
public class Tutor {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @OneToOne
    @JoinColumn(name= "user_id", nullable = false, unique = true)
    private User user;

    private String bio;
    private String subjects;
    private String location;
    private boolean remote;
    private Integer hourlyRate;
}
