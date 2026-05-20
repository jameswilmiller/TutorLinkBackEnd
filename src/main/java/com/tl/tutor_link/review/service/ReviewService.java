package com.tl.tutor_link.review.service;

import com.tl.tutor_link.booking.model.Booking;
import com.tl.tutor_link.booking.model.BookingStatus;
import com.tl.tutor_link.booking.repository.BookingRepository;
import com.tl.tutor_link.common.exception.BadRequestException;
import com.tl.tutor_link.common.exception.ConflictException;
import com.tl.tutor_link.common.exception.ForbiddenException;
import com.tl.tutor_link.common.exception.ResourceNotFoundException;
import com.tl.tutor_link.notification.service.NotificationService;
import com.tl.tutor_link.review.dto.ReviewDto;
import com.tl.tutor_link.review.dto.ReviewRequestDto;
import com.tl.tutor_link.review.mapper.ReviewMapper;
import com.tl.tutor_link.review.model.Review;
import com.tl.tutor_link.review.repository.ReviewRepository;
import com.tl.tutor_link.tutor.model.Tutor;
import com.tl.tutor_link.tutor.repository.TutorRepository;
import com.tl.tutor_link.user.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Review creation and retrieval. A review can only be created by the
 * student of a COMPLETED booking, and only once per booking (enforced
 * both here and by a unique constraint on the table). Notifies the tutor
 * when a review is left.
 */
@Service
public class ReviewService {

    private static final Logger log = LoggerFactory.getLogger(ReviewService.class);

    private final ReviewRepository reviewRepository;
    private final BookingRepository bookingRepository;
    private final TutorRepository tutorRepository;
    private final ReviewMapper reviewMapper;
    private final NotificationService notificationService;

    public ReviewService(
            ReviewRepository reviewRepository,
            BookingRepository bookingRepository,
            TutorRepository tutorRepository,
            ReviewMapper reviewMapper,
            NotificationService notificationService
    ) {
        this.reviewRepository = reviewRepository;
        this.bookingRepository = bookingRepository;
        this.tutorRepository = tutorRepository;
        this.reviewMapper = reviewMapper;
        this.notificationService = notificationService;
    }

    // -----------------------------------------------------------------
    // Creation
    // -----------------------------------------------------------------

    @Transactional
    public ReviewDto createReview(ReviewRequestDto dto, User student) {
        log.info("User {} reviewing booking {}", student.getId(), dto.getBookingId());

        Booking booking = bookingRepository.findById(dto.getBookingId())
                .orElseThrow(() -> new ResourceNotFoundException("Booking", dto.getBookingId()));

        if (!booking.getStudent().getId().equals(student.getId())) {
            throw new ForbiddenException("You can only review your own bookings");
        }

        if (booking.getStatus() != BookingStatus.COMPLETED) {
            throw new BadRequestException("You can only review a completed booking");
        }

        if (reviewRepository.existsByBookingId(booking.getId())) {
            throw new ConflictException("This booking has already been reviewed");
        }

        Review review = new Review();
        review.setBooking(booking);
        review.setTutor(booking.getTutor());
        review.setStudent(student);
        review.setRating(dto.getRating());
        review.setComment(dto.getComment());

        Review saved = reviewRepository.save(review);
        log.info("Review {} created for tutor {}", saved.getId(), booking.getTutor().getId());

        applyReviewToTutorAggregates(booking.getTutor(), dto.getRating());

        notifyTutorOfNewReview(saved);
        return reviewMapper.toDto(saved);
    }

    // -----------------------------------------------------------------
    // Reads
    // -----------------------------------------------------------------

    @Transactional(readOnly = true)
    public List<ReviewDto> getTutorReviews(Long tutorId) {
        log.debug("Fetching reviews for tutor {}", tutorId);

        Tutor tutor = tutorRepository.findById(tutorId)
                .orElseThrow(() -> new ResourceNotFoundException("Tutor", tutorId));

        return reviewRepository.findByTutorOrderByCreatedAtDesc(tutor)
                .stream()
                .map(reviewMapper::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ReviewDto> getMyReviews(User student) {
        log.debug("Fetching reviews written by user {}", student.getId());
        return reviewRepository.findByStudentOrderByCreatedAtDesc(student)
                .stream()
                .map(reviewMapper::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public boolean bookingHasReview(Long bookingId) {
        return reviewRepository.existsByBookingId(bookingId);
    }


    // -----------------------------------------------------------------
    // Notifications
    // -----------------------------------------------------------------

    private void notifyTutorOfNewReview(Review review) {
        notificationService.send(
                review.getTutor().getUser().getEmail(),
                ReviewEmails.newReviewSubject(),
                ReviewEmails.newReviewBody(review),
                "new review notification"
        );
    }

    // -----------------------------------------------------------------
    // Tutor rating aggregates
    // -----------------------------------------------------------------

    /**
     * Incrementally updates the tutor's stored rating aggregates after a new review

     */
    private void applyReviewToTutorAggregates(Tutor tutor, int rating) {
        int newCount = tutor.getReviewCount() + 1;
        int newTotal = tutor.getRatingTotal() + rating;

        tutor.setReviewCount(newCount);
        tutor.setRatingTotal(newTotal);
        tutor.setAverageRating((double) newTotal / newCount);
    }
}