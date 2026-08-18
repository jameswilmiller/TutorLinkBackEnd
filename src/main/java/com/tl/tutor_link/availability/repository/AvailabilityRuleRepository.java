package com.tl.tutor_link.availability.repository;

import com.tl.tutor_link.availability.model.AvailabilityRule;
import com.tl.tutor_link.tutor.model.Tutor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AvailabilityRuleRepository extends JpaRepository<AvailabilityRule, Long> {

    List<AvailabilityRule> findByTutorOrderByDayOfWeekAscStartTimeAsc(Tutor tutor);

    void deleteByTutor(Tutor tutor);
}
