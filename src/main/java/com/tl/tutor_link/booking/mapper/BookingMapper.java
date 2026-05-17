package com.tl.tutor_link.booking.mapper;

import com.tl.tutor_link.booking.dto.BookingDto;
import com.tl.tutor_link.booking.model.Booking;
import com.tl.tutor_link.tutor.model.Tutor;
import com.tl.tutor_link.user.model.User;
import org.springframework.stereotype.Component;

/**
 * Maps Booking entities to BookingDto.
 */
@Component
public class BookingMapper {

    public BookingDto toDto(Booking booking) {
        if (booking == null) {
            return null;
        }

        BookingDto dto = new BookingDto();
        dto.setId(booking.getId());

        Tutor tutor = booking.getTutor();
        User tutorUser = tutor.getUser();
        dto.setTutorId(tutor.getId());
        dto.setTutorName(tutorUser.getFirstname() + " " + tutorUser.getLastname());
        dto.setTutorEmail(tutorUser.getEmail());

        User student = booking.getStudent();
        dto.setStudentId(student.getId());
        dto.setStudentName(student.getFirstname() + " " + student.getLastname());
        dto.setStudentEmail(student.getEmail());

        dto.setCourseId(booking.getCourse().getId());
        dto.setCourseCode(booking.getCourse().getCourseCode());
        dto.setCourseName(booking.getCourse().getCourseName());

        dto.setScheduledAt(booking.getScheduledAt());
        dto.setDurationMinutes(booking.getDurationMinutes());
        dto.setMeetingLink(booking.getMeetingLink());
        dto.setSessionType(booking.getSessionType());
        dto.setStatus(booking.getStatus());
        dto.setMessage(booking.getMessage());
        dto.setCreatedAt(booking.getCreatedAt());

        return dto;
    }
}