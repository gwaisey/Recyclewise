package com.recyclewise.service;

import com.recyclewise.exception.ResourceNotFoundException;
import com.recyclewise.model.WasteItem;
import com.recyclewise.repository.WasteItemRepository;
import com.recyclewise.service.impl.WasteItemServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for WasteItemServiceImpl.
 *
 * OOP   — Polymorphism in action: WasteItemRepository is mocked (substituted) without
 *          changing any service code — possible only because we depend on the interface.
 * SOLID — (D) proves that controllers/tests can inject any WasteItemService implementation.
 */
@ExtendWith(MockitoExtension.class)
class WasteItemServiceImplTest {

    @Mock
    private WasteItemRepository wasteItemRepository;

    @InjectMocks
    private WasteItemServiceImpl wasteItemService;

    private WasteItem bottle;
    private WasteItem battery;

    @BeforeEach
    void setUp() {
        bottle = WasteItem.builder()
                .id(1L).name("Plastic Bottle").category("RECYCLABLE")
                .icon("🍶").recyclable(true).binColor("blue")
                .disposalInstructions("Rinse and flatten.").tips("Avoid single-use plastic.")
                .build();

        battery = WasteItem.builder()
                .id(2L).name("Old Battery").category("HAZARDOUS")
                .icon("🔋").recyclable(false).binColor("red")
                .disposalInstructions("Take to hazardous waste facility.").tips("Check store drop-offs.")
                .build();
    }

    // ------------------------------------------------------------------
    // getAllWasteItems
    // ------------------------------------------------------------------

    @Test
    @DisplayName("getAllWasteItems — returns all items from repository")
    void getAllWasteItems_returnsAll() {
        when(wasteItemRepository.findAll()).thenReturn(List.of(bottle, battery));

        List<WasteItem> result = wasteItemService.getAllWasteItems();

        assertThat(result).hasSize(2);
        assertThat(result).extracting(WasteItem::getName)
                .containsExactlyInAnyOrder("Plastic Bottle", "Old Battery");
        verify(wasteItemRepository, times(1)).findAll();
    }

    // ------------------------------------------------------------------
    // getWasteItemsByCategory
    // ------------------------------------------------------------------

    @Test
    @DisplayName("getWasteItemsByCategory — filters correctly by category")
    void getWasteItemsByCategory_filtersCorrectly() {
        when(wasteItemRepository.findByCategory("RECYCLABLE")).thenReturn(List.of(bottle));

        List<WasteItem> result = wasteItemService.getWasteItemsByCategory("recyclable"); // lowercase input

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getCategory()).isEqualTo("RECYCLABLE");
        verify(wasteItemRepository).findByCategory("RECYCLABLE");
    }

    // ------------------------------------------------------------------
    // searchWasteItems
    // ------------------------------------------------------------------

    @Test
    @DisplayName("searchWasteItems — returns results for valid query")
    void searchWasteItems_validQuery_returnsResults() {
        when(wasteItemRepository.searchByName("bottle")).thenReturn(List.of(bottle));

        List<WasteItem> result = wasteItemService.searchWasteItems("bottle");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Plastic Bottle");
    }

    @Test
    @DisplayName("searchWasteItems — blank query returns all items")
    void searchWasteItems_blankQuery_returnsAll() {
        when(wasteItemRepository.findAll()).thenReturn(List.of(bottle, battery));

        List<WasteItem> result = wasteItemService.searchWasteItems("   ");

        assertThat(result).hasSize(2);
        verify(wasteItemRepository).findAll();
        verify(wasteItemRepository, never()).searchByName(any());
    }

    @Test
    @DisplayName("searchWasteItems — null query returns all items")
    void searchWasteItems_nullQuery_returnsAll() {
        when(wasteItemRepository.findAll()).thenReturn(List.of(bottle, battery));

        List<WasteItem> result = wasteItemService.searchWasteItems(null);

        assertThat(result).hasSize(2);
    }

    // ------------------------------------------------------------------
    // getWasteItemById
    // ------------------------------------------------------------------

    @Test
    @DisplayName("getWasteItemById — returns item when found")
    void getWasteItemById_found_returnsItem() {
        when(wasteItemRepository.findById(1L)).thenReturn(Optional.of(bottle));

        WasteItem result = wasteItemService.getWasteItemById(1L);

        assertThat(result.getName()).isEqualTo("Plastic Bottle");
        assertThat(result.isRecyclable()).isTrue();
    }

    @Test
    @DisplayName("getWasteItemById — throws ResourceNotFoundException when not found")
    void getWasteItemById_notFound_throwsException() {
        when(wasteItemRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> wasteItemService.getWasteItemById(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("WasteItem")
                .hasMessageContaining("99");
    }

    // ------------------------------------------------------------------
    // getWasteStatsByCategory
    // ------------------------------------------------------------------

    @Test
    @DisplayName("getWasteStatsByCategory — returns correct counts per category")
    void getWasteStatsByCategory_returnsCorrectCounts() {
        WasteItem bottle2 = WasteItem.builder().id(3L).name("Glass Jar").category("RECYCLABLE").build();
        when(wasteItemRepository.findAll()).thenReturn(List.of(bottle, battery, bottle2));

        Map<String, Long> stats = wasteItemService.getWasteStatsByCategory();

        assertThat(stats).containsEntry("RECYCLABLE", 2L);
        assertThat(stats).containsEntry("HAZARDOUS", 1L);
    }
}
