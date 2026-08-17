package com.tl.tutor_link.availability.mapper;

import com.tl.tutor_link.availability.dto.AvailabilityRuleDto;
import com.tl.tutor_link.availability.model.AvailabilityRule;
import com.tl.tutor_link.common.mapper.Mapper;
import org.springframework.stereotype.Component;

@Component
public class AvailabilityRuleMapper implements Mapper<AvailabilityRule, AvailabilityRuleDto> {

    @Override
    public AvailabilityRuleDto toDto(AvailabilityRule rule) {

        if (rule == null) {
            return null;
        }

        AvailabilityRuleDto dto = new AvailabilityRuleDto();
        dto.setDayOfWeek(rule.getDayOfWeek());
        dto.setStartTime(rule.getStartTime());
        dto.setEndTime(rule.getEndTime());
        return dto;
    }
}
