package com.tl.tutor_link.tutor.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EnquiryRequestDto {
    private String course;
    private String sessionType;
    private String message;
}