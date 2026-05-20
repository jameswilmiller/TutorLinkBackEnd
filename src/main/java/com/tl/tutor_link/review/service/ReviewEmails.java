package com.tl.tutor_link.review.service;

import com.tl.tutor_link.review.model.Review;
import com.tl.tutor_link.user.model.User;
import org.springframework.web.util.HtmlUtils;

/**
 * Builds the HTML body and subject for review-related emails.
 */
final class ReviewEmails {

    private ReviewEmails() {
        // static-only
    }

    static String newReviewSubject() {
        return "You received a new review on TutorLink";
    }

    static String newReviewBody(Review review) {
        return "<h2>New review</h2>"
                + "<p>" + escape(fullName(review.getStudent()))
                + " left you a " + review.getRating() + "-star review"
                + " for " + escape(review.getBooking().getCourse().getCourseCode()) + ".</p>"
                + commentBlock(review)
                + "<hr><p>Log in to TutorLink to view your reviews.</p>";
    }

    private static String fullName(User user) {
        return user.getFirstname() + " " + user.getLastname();
    }

    private static String commentBlock(Review review) {
        if (review.getComment() == null || review.getComment().isBlank()) {
            return "";
        }
        return "<p><strong>Their comment:</strong></p><p>"
                + escape(review.getComment()) + "</p>";
    }

    private static String escape(String value) {
        return value == null ? "" : HtmlUtils.htmlEscape(value);
    }
}