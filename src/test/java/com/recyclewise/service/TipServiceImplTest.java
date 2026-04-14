package com.recyclewise.service;

import com.recyclewise.model.RecyclingTip;
import com.recyclewise.repository.RecyclingTipRepository;
import com.recyclewise.service.impl.TipServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

/**
 * Unit tests for TipServiceImpl.
 *
 * Demonstrates DIP: TipService interface is tested here —
 * the concrete TipServiceImpl can be swapped without changing the test contract.
 */
@ExtendWith(MockitoExtension.class)
class TipServiceImplTest {

    @Mock
    private RecyclingTipRepository tipRepository;

    @InjectMocks
    private TipServiceImpl tipService;

    private RecyclingTip compostTip;
    private RecyclingTip reduceTip;

    @BeforeEach
    void setUp() {
        compostTip = RecyclingTip.builder()
                .id(1L).title("Start a Home Compost").category("COMPOST")
                .icon("🌱").description("Great for the garden.").impactScore(10)
                .build();

        reduceTip = RecyclingTip.builder()
                .id(2L).title("Buy in Bulk").category("REDUCE")
                .icon("🛒").description("Less packaging waste.").impactScore(8)
                .build();
    }

    @Test
    @DisplayName("getAllTips — returns every tip")
    void getAllTips_returnsAll() {
        when(tipRepository.findAll()).thenReturn(List.of(compostTip, reduceTip));

        List<RecyclingTip> result = tipService.getAllTips();

        assertThat(result).hasSize(2);
        verify(tipRepository).findAll();
    }

    @Test
    @DisplayName("getTopTips — delegates to top-6 query")
    void getTopTips_callsTopQuery() {
        when(tipRepository.findTop6ByOrderByImpactScoreDesc()).thenReturn(List.of(compostTip));

        List<RecyclingTip> result = tipService.getTopTips();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getImpactScore()).isEqualTo(10);
        verify(tipRepository).findTop6ByOrderByImpactScoreDesc();
    }

    @Test
    @DisplayName("getTipsByCategory — filters by uppercase category")
    void getTipsByCategory_filtersCorrectly() {
        when(tipRepository.findByCategory("COMPOST")).thenReturn(List.of(compostTip));

        List<RecyclingTip> result = tipService.getTipsByCategory("compost"); // lowercase input

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getCategory()).isEqualTo("COMPOST");
        verify(tipRepository).findByCategory("COMPOST");
    }
}
