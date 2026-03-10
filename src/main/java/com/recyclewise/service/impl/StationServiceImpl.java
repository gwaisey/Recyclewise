package com.recyclewise.service.impl;

import com.recyclewise.exception.ResourceNotFoundException;
import com.recyclewise.model.TrashStation;
import com.recyclewise.repository.TrashStationRepository;
import com.recyclewise.service.StationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StationServiceImpl implements StationService {

    private final TrashStationRepository stationRepository;

    @Override
    public List<TrashStation> getAllActiveStations() {
        return stationRepository.findByActiveTrue();
    }

    @Override
    public TrashStation findById(Long id) {
        return stationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("TrashStation", "id", id));
    }
}
