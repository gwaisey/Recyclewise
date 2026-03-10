package com.recyclewise.service.impl;

import com.recyclewise.exception.ResourceNotFoundException;
import com.recyclewise.model.*;
import com.recyclewise.repository.RedemptionRepository;
import com.recyclewise.repository.RewardRepository;
import com.recyclewise.service.RewardService;
import com.recyclewise.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * SOLID — (S) SRP: handles reward catalogue + redemption logic only
 * SOLID — (D) DIP: depends on UserService interface, not UserServiceImpl
 */
@Service
@RequiredArgsConstructor
public class RewardServiceImpl implements RewardService {

    private final RewardRepository rewardRepository;
    private final RedemptionRepository redemptionRepository;
    private final UserService userService;

    @Override
    public List<Reward> getAllAvailableRewards() {
        return rewardRepository.findByAvailableTrueOrderByPointsCostAsc();
    }

    @Override
    public Reward findById(Long id) {
        return rewardRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reward", "id", id));
    }

    @Override
    @Transactional
    public Redemption redeem(Long userId, Long rewardId) {
        User user = userService.findById(userId);
        Reward reward = findById(rewardId);

        if (!reward.isAvailable() || reward.getStockRemaining() <= 0) {
            throw new IllegalArgumentException("Reward is no longer available.");
        }
        if (user.getTotalPoints() < reward.getPointsCost()) {
            throw new IllegalArgumentException(
                "Not enough points. You need " + reward.getPointsCost() +
                " pts but have " + user.getTotalPoints() + " pts.");
        }

        // Deduct points
        userService.deductPoints(user, reward.getPointsCost());

        // Reduce stock
        reward.setStockRemaining(reward.getStockRemaining() - 1);
        if (reward.getStockRemaining() == 0) reward.setAvailable(false);
        rewardRepository.save(reward);

        // Create redemption with unique voucher code
        Redemption redemption = Redemption.builder()
                .user(user)
                .reward(reward)
                .pointsSpent(reward.getPointsCost())
                .voucherCode(generateVoucherCode())
                .status(Redemption.RedemptionStatus.ACTIVE)
                .build();

        return redemptionRepository.save(redemption);
    }

    @Override
    public List<Redemption> getRedemptionsForUser(User user) {
        return redemptionRepository.findByUserOrderByRedeemedAtDesc(user);
    }

    private String generateVoucherCode() {
        return "RW-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}
