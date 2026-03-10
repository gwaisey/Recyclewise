package com.recyclewise.service;

import com.recyclewise.model.RecyclingTip;

import java.util.List;

/**
 * Service interface for RecyclingTip operations.
 *
 * SOLID — (I) Interface Segregation: separate from WasteItemService so
 *         TipsController only depends on tip-related methods, nothing more.
 * SOLID — (D) Dependency Inversion: TipsController depends on this interface,
 *         not on TipServiceImpl directly.
 */
public interface TipService {

    List<RecyclingTip> getAllTips();

    List<RecyclingTip> getTopTips();

    List<RecyclingTip> getTipsByCategory(String category);
}
