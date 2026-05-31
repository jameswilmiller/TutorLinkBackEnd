package com.tl.tutor_link.tutor.repository;

import com.tl.tutor_link.tutor.model.Tutor;
import com.tl.tutor_link.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TutorRepository extends JpaRepository<Tutor, Long>, JpaSpecificationExecutor<Tutor> {

    Optional<Tutor> findByUser(User user);
    boolean existsBySlug(String slug);
    Optional<Tutor> findBySlug(String slug);
    /**
     * Returns IDs of tutors within the given radius of the provided coordinates.
     * Uses the Haversine formula directly in SQL.
     */
    @Query(value = """
        SELECT t.id FROM tutors t
        WHERE t.latitude IS NOT NULL
          AND t.longitude IS NOT NULL
          AND (
              6371 * acos(
                  cos(radians(:lat)) * cos(radians(t.latitude)) *
                  cos(radians(t.longitude) - radians(:lng)) +
                  sin(radians(:lat)) * sin(radians(t.latitude))
              )
          ) <= :maxKm
        """, nativeQuery = true)
    List<Long> findIdsWithinDistance(
            @Param("lat") double lat,
            @Param("lng") double lng,
            @Param("maxKm") double maxKm
    );

}
