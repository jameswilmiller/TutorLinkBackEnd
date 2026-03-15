package com.tl.tutor_link.repository;

import com.tl.tutor_link.model.Tutor;
import com.tl.tutor_link.model.User;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TutorRepository {
    Optional<Tutor> findByUser(User user);
    List<Tutor> findAll();
}
