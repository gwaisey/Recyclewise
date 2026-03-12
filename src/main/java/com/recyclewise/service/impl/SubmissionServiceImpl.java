package com.recyclewise.service.impl;

import com.recyclewise.model.*;
import com.recyclewise.repository.SubmissionItemRepository;
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

    @Override
    @Transactional
    public TrashSubmission submit(Long userId, Long stationId, Map<String, Double> categoryWeights) {
        User user = userService.findById(userId);
        TrashStation station = stationService.findById(stationId);

        // Build submission
        TrashSubmission submission = TrashSubmission.builder()
                .user(user)
                .station(station)
                .status(TrashSubmission.SubmissionStatus.PENDING)
                .build();

        TrashSubmission saved = submissionRepository.save(submission);

        // Build line items + calculate points
        List<SubmissionItem> items = new ArrayList<>();
        int totalPoints = 0;

        for (Map.Entry<String, Double> entry : categoryWeights.entrySet()) {
            String category = entry.getKey();
            double weight = entry.getValue();
            if (weight <= 0) continue;

            int pts = pointsCalculator.calculatePoints(category, weight);
            totalPoints += pts;

            items.add(SubmissionItem.builder()
                    .submission(saved)
                    .category(category)
                    .weightKg(weight)
                    .pointsEarned(pts)
                    .build());
        }

        saved.setItems(items);
        saved.setPointsEarned(totalPoints);
        submissionRepository.save(saved);

        // Award points immediately on submission (confirmed on arrival)
        // Points are NOT awarded here — only when admin confirms the submission

        return saved;
    }

    @Override
    public List<TrashSubmission> getSubmissionsForUser(User user) {
        return submissionRepository.findByUserOrderBySubmittedAtDesc(user);
    }
}
