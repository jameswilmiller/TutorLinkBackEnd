package com.tl.tutor_link.tutor.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

/**
 * A UQ course. Identified by its course code (e.g. "CSSE2010") which is
 * unique across the catalogue. Tutors are associated with courses via a
 * many-to-many relationship.
 */
@Entity
@Table(name = "courses")
@Getter
@Setter
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(unique = true, nullable = false)
    private String courseCode;

    @Column(nullable = false)
    private String courseName;

    @Column(nullable = false)
    private String faculty;
}