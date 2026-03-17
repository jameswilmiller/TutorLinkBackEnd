package com.tl.tutor_link.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

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

    @Column
    private String bio;
    private String subjects;
    private String location;
    private boolean remote;
    private Integer hourlyRate;
}
