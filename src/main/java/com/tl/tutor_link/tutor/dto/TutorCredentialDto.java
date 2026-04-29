package com.tl.tutor_link.tutor.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TutorCredentialDto {
    private Long id;
    private String title;
    private String institution;
    private Integer year;
}