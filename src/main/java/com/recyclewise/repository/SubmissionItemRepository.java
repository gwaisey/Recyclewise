package com.recyclewise.repository;

import com.recyclewise.model.SubmissionItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SubmissionItemRepository extends JpaRepository<SubmissionItem, Long> {
}
