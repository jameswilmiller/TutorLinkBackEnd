package com.tl.tutor_link.review.repository;

import com.tl.tutor_link.review.model.Review;
import com.tl.tutor_link.tutor.model.Tutor;
import com.tl.tutor_link.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    List<Review> findByTutorOrderByCreatedAtDesc(Tutor tutor);

    List<Review> findByStudentOrderByCreatedAtDesc(User student);

    boolean existsByBookingId(Long bookingId);
}