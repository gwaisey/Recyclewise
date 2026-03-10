package com.recyclewise.service;

import com.recyclewise.model.TrashSubmission;
import com.recyclewise.model.User;

import java.util.List;
import java.util.Map;

/**
 * SOLID — (I) ISP: only submission operations
 */
public interface SubmissionService {
    TrashSubmission submit(Long userId, Long stationId, Map<String, Double> categoryWeights);
    List<TrashSubmission> getSubmissionsForUser(User user);
}
