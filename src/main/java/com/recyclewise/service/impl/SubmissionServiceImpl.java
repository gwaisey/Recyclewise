package com.recyclewise.service.impl;

import com.recyclewise.model.*;
import java.util.Objects;
import com.recyclewise.repository.TrashSubmissionRepository;
import com.recyclewise.service.PointsCalculator;
import com.recyclewise.service.StationService;
import com.recyclewise.service.SubmissionService;
import com.recyclewise.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Core submission logic:
 * 1. Validate user + station exist
 * 2. For each category + weight → calculate points via PointsCalculator
 * 3. Save submission + line items
 * 4. Add points to user balance
 *
 * SOLID — (S) SRP: only handles submission workflow
 * SOLID — (D) DIP: depends on service interfaces, not concrete classes
 * OOP   — Encapsulation: point calculation is delegated to PointsCalculator
 */
@Service
@RequiredArgsConstructor
public class SubmissionServiceImpl implements SubmissionService {

    private final TrashSubmissionRepository submissionRepository;
    private final UserService userService;
    private final StationService stationService;
    private final PointsCalculator pointsCalculator;

    private static final double MAX_WEIGHT_PER_ITEM = 100.0; // 100 kg limit per category
    private static final int MAX_POINTS_PER_SUBMISSION = 5000; // Abuse prevention cap per session

    @Override
    @Transactional
    public TrashSubmission submit(Long userId, Long stationId, Map<String, Double> categoryWeights) {
        User user = userService.findById(userId);
        TrashStation station = stationService.findById(stationId);

        // Build line items + calculate points
        List<SubmissionItem> items = new ArrayList<>();
        int totalPoints = 0;

        for (Map.Entry<String, Double> entry : categoryWeights.entrySet()) {
            String category = entry.getKey();
            Double weight = entry.getValue();
            
            // Defensively handle null, NaN, and Infinity
            if (weight == null || !Double.isFinite(weight) || Double.isNaN(weight) || weight <= 0) {
                continue; 
            }

            // Validate weight limit (Defense-in-depth against extreme values)
            if (weight > MAX_WEIGHT_PER_ITEM) {
                throw new IllegalArgumentException("Weight for " + category + " exceeds the safety limit of " + MAX_WEIGHT_PER_ITEM + "kg.");
            }

            int pts = pointsCalculator.calculatePoints(category, weight);
            totalPoints += pts;

            items.add(SubmissionItem.builder()
                    .category(category)
                    .weightKg(weight)
                    .pointsEarned(pts)
                    .build());
        }
        
        // Validate at least one item and non-zero points
        if (items.isEmpty() || totalPoints <= 0) {
            throw new IllegalArgumentException("Submission must contain at least one valid item with positive weight.");
        }

        // Validate points cap
        if (totalPoints > MAX_POINTS_PER_SUBMISSION) {
             throw new IllegalArgumentException("Total estimated points (" + totalPoints + ") exceed the safety limit per submission.");
        }

        // Build submission
        TrashSubmission submission = TrashSubmission.builder()
                .user(user)
                .station(station)
                .status(TrashSubmission.SubmissionStatus.PENDING)
                .items(new ArrayList<>()) // Items assigned via list
                .pointsEarned(totalPoints)
                .build();
        
        TrashSubmission saved = submissionRepository.save(Objects.requireNonNull(submission));
        
        // Link items to saved submission
        for (SubmissionItem item : items) {
            item.setSubmission(saved);
        }
        saved.setItems(items);
        
        return submissionRepository.save(Objects.requireNonNull(saved));
    }

    @Override
    public List<TrashSubmission> getSubmissionsForUser(User user) {
        return submissionRepository.findByUserOrderBySubmittedAtDesc(user);
    }
}
