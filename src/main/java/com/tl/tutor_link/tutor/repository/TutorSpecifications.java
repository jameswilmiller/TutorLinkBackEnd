package com.tl.tutor_link.tutor.repository;

import com.tl.tutor_link.tutor.model.Course;
import com.tl.tutor_link.tutor.model.Faculty;
import com.tl.tutor_link.tutor.model.Tutor;
import jakarta.persistence.criteria.Join;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

public class TutorSpecifications {

    private TutorSpecifications() {
        // utility class - no instances
    }

    /**
     * Filters tutors who teach the given course code (case-insensitive).
     * Uses a JOIN to the courses table.
     */
    public static Specification<Tutor> hasCourseCode(String courseCode) {
        return (root, query, cb) -> {
            if (courseCode == null || courseCode.isBlank()) {
                return cb.conjunction(); // matches everything (no filter)
            }
            query.distinct(true);
            Join<Tutor, Course> courses = root.join("courses");
            return cb.equal(cb.lower(courses.get("courseCode")), courseCode.toLowerCase());
        };
    }

    /**
     * Filters tutors who belong to the given faculty.
     */
    public static Specification<Tutor> hasFaculty(Faculty faculty) {
        return (root, query, cb) -> {
            if (faculty == null) {
                return cb.conjunction();
            }
            return cb.isMember(faculty, root.get("faculties"));
        };
    }

    /**
     * Filters tutors whose location contains the given string (case-insensitive).
     */
    public static Specification<Tutor> locationContains(String location) {
        return (root, query, cb) -> {
            if (location == null || location.isBlank()) {
                return cb.conjunction();
            }
            return cb.like(cb.lower(root.get("location")), "%" + location.toLowerCase() + "%");
        };
    }

    /**
     * Filters tutors by remote/in-person availability.
     */
    public static Specification<Tutor> isRemote(Boolean remote) {
        return (root, query, cb) -> {
            if (remote == null) {
                return cb.conjunction();
            }
            return cb.equal(root.get("remote"), remote);
        };
    }

    /**
     * Filters tutors whose IDs are in the given list. Used to combine with
     * the distance query, which returns only the IDs of nearby tutors.
     */
    public static Specification<Tutor> idIn(List<Long> ids) {
        return (root, query, cb) -> {
            if (ids == null) return cb.conjunction();
            if (ids.isEmpty()) return cb.disjunction(); // empty list = match nothing
            return root.get("id").in(ids);
        };
    }
}