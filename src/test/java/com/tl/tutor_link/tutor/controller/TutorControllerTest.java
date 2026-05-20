package com.tl.tutor_link.tutor.controller;

import com.tl.tutor_link.support.IntegrationTestBase;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;

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
}