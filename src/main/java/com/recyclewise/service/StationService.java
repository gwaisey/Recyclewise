package com.recyclewise.service;

import com.recyclewise.model.TrashStation;
import java.util.List;

/**
 * SOLID — (I) ISP: only station-related operations
 */
public interface StationService {
    List<TrashStation> getAllActiveStations();
    TrashStation findById(Long id);
}
