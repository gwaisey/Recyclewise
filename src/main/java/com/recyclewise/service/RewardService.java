package com.recyclewise.service;

import com.recyclewise.model.Redemption;
import com.recyclewise.model.Reward;
import com.recyclewise.model.User;

import java.util.List;

/**
 * SOLID — (I) ISP: only reward/redemption operations
 */
public interface RewardService {
    List<Reward> getAllAvailableRewards();
    Reward findById(Long id);
    Redemption redeem(Long userId, Long rewardId);
    List<Redemption> getRedemptionsForUser(User user);
}
