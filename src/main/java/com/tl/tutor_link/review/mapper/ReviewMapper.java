package com.tl.tutor_link.review.mapper;

import com.tl.tutor_link.review.dto.ReviewDto;
import com.tl.tutor_link.review.model.Review;
import com.tl.tutor_link.user.model.User;
import org.springframework.stereotype.Component;

/**
 * Maps Review entities to ReviewDto.
 */
@Component
public class ReviewMapper {

    public ReviewDto toDto(Review review) {
        if (review == null) {
            return null;
        }

        ReviewDto dto = new ReviewDto();
        dto.setId(review.getId());

        dto.setTutorId(review.getTutor().getId());

        User student = review.getStudent();
        dto.setStudentId(student.getId());
        dto.setStudentName(student.getFirstname() + " " + student.getLastname());

        dto.setBookingId(review.getBooking().getId());
        dto.setCourseCode(review.getBooking().getCourse().getCourseCode());

        dto.setRating(review.getRating());
        dto.setComment(review.getComment());
        dto.setCreatedAt(review.getCreatedAt());

        return dto;
    }
}