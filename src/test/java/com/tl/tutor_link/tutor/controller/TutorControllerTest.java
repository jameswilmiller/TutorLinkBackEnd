package com.tl.tutor_link.tutor.controller;

import com.tl.tutor_link.support.IntegrationTestBase;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

import org.springframework.test.web.servlet.client.RestTestClient;

class TutorControllerTest extends IntegrationTestBase {

    @Autowired
    private RestTestClient restTestClient;

    @Test
    void getTutors_whenNoTutorsExist_returnsEmptyPage() {

        restTestClient.get()
                .uri("/tutors")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.content.length()")
                .isEqualTo(0);
    }

    @Test
    void createTutorProfile_whenAnonymous_isRejected() {

        restTestClient.post()
                .uri("/tutors/me/profile")
                .contentType(MediaType.APPLICATION_JSON)
                .body("{\"bio\":\"anonymous attempt\"}")
                .exchange()
                .expectStatus().is4xxClientError();
    }

    @Test
    void getMyTutorProfile_whenAnonymous_isRejected() {

        restTestClient.get()
                .uri("/tutors/me/profile")
                .exchange()
                .expectStatus().is4xxClientError();
    }
}