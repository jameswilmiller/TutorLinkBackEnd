package com.tl.tutor_link.booking.repository;

import com.tl.tutor_link.booking.model.Booking;
import com.tl.tutor_link.tutor.model.Tutor;
import com.tl.tutor_link.user.model.User;
import com.tl.tutor_link.booking.model.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Collection;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findByStudentOrderByScheduledAtDesc(User student);

    List<Booking> findByTutorOrderByScheduledAtDesc(Tutor tutor);

    List<Booking> findByTutorAndStatusInAndScheduledAtBetween(
            Tutor tutor,
            Collection<BookingStatus> statuses,
            LocalDateTime from,
            LocalDateTime to
    );
}