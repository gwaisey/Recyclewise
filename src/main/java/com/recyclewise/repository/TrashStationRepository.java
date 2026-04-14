package com.recyclewise.repository;

import com.recyclewise.model.TrashStation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TrashStationRepository extends JpaRepository<TrashStation, Long> {
    List<TrashStation> findByActiveTrue();
    List<TrashStation> findByDistrict(String district);
}
