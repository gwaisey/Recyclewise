package com.recyclewise.repository;

import com.recyclewise.model.TrashSubmission;
import com.recyclewise.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface TrashSubmissionRepository extends JpaRepository<TrashSubmission, Long> {

    List<TrashSubmission> findByUserOrderByCreatedAtDesc(User user);

    List<TrashSubmission> findByStatusOrderByCreatedAtDesc(TrashSubmission.SubmissionStatus status);

    List<TrashSubmission> findByStatusNotOrderByCreatedAtDesc(TrashSubmission.SubmissionStatus status);

    List<TrashSubmission> findByStationIdAndStatusOrderByCreatedAtDesc(Long stationId, TrashSubmission.SubmissionStatus status);

    List<TrashSubmission> findByStationIdAndStatusNotOrderByCreatedAtDesc(Long stationId, TrashSubmission.SubmissionStatus status);

    @Query("SELECT SUM(s.pointsEarned) FROM TrashSubmission s WHERE s.user = :user AND s.status = 'CONFIRMED'")
    Integer sumConfirmedPointsByUser(@Param("user") User user);
}
