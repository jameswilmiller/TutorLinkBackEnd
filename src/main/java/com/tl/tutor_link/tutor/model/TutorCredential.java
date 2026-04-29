package com.tl.tutor_link.tutor.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name="tutor_credentials")
@Getter
@Setter
public class TutorCredential {
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private long id;

    @ManyToOne
    @JoinColumn(name="tutor_id", nullable = false)
    private Tutor tutor;

    private String title;
    private String institution;
    private Integer year;
}
