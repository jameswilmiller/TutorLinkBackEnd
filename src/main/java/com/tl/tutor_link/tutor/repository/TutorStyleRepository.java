package com.tl.tutor_link.tutor.repository;

import com.tl.tutor_link.tutor.model.Tutor;
import com.tl.tutor_link.tutor.model.TutorStyle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TutorStyleRepository extends JpaRepository<TutorStyle, Long> {
    void deleteAllByTutor(Tutor tutor);
}