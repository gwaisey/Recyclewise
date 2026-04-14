package com.recyclewise.service.impl;

import com.recyclewise.model.RecyclingTip;
import com.recyclewise.repository.RecyclingTipRepository;
import com.recyclewise.service.TipService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Concrete implementation of TipService.
 *
 * OOP  — Encapsulation: repository is private; accessed only via this service
 * SOLID — (S) Single Responsibility: handles ONLY recycling tips logic
 * SOLID — (D) Dependency Inversion: depends on RecyclingTipRepository abstraction
 */
@Service
@RequiredArgsConstructor
public class TipServiceImpl implements TipService {

    private final RecyclingTipRepository tipRepository;

    @Override
    public List<RecyclingTip> getAllTips() {
        return tipRepository.findAll();
    }

    @Override
    public List<RecyclingTip> getTopTips() {
        return tipRepository.findTop6ByOrderByImpactScoreDesc();
    }

    @Override
    public List<RecyclingTip> getTipsByCategory(String category) {
        return tipRepository.findByCategory(category.toUpperCase());
    }
}
