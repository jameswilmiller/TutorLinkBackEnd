package com.tl.tutor_link.tutor.repository;

import com.tl.tutor_link.tutor.model.Tutor;
import com.tl.tutor_link.tutor.model.TutorCredential;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TutorCredentialRepository extends JpaRepository<TutorCredential, Long> {
    void deleteAllByTutor(Tutor tutor);
}