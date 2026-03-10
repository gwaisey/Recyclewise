package com.recyclewise.repository;

import com.recyclewise.model.RecyclingTip;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RecyclingTipRepository extends JpaRepository<RecyclingTip, Long> {
    List<RecyclingTip> findByCategory(String category);
    List<RecyclingTip> findTop6ByOrderByImpactScoreDesc();
}
