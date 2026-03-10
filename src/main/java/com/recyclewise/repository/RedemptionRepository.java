package com.recyclewise.repository;

import com.recyclewise.model.Redemption;
import com.recyclewise.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RedemptionRepository extends JpaRepository<Redemption, Long> {
    List<Redemption> findByUserOrderByRedeemedAtDesc(User user);
}
