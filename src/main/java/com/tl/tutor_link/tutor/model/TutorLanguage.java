package com.tl.tutor_link.tutor.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name="tutor_languages")
@Getter
@Setter
public class TutorLanguage {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private long id;

    @ManyToOne
    @JoinColumn(name="tutor_id", nullable=false)
    private Tutor tutor;

    private String language;

    @Enumerated(EnumType.STRING)
    private LanguageLevel level;


}
