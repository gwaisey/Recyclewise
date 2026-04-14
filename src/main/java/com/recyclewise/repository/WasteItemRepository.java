package com.recyclewise.repository;

import com.recyclewise.model.WasteItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WasteItemRepository extends JpaRepository<WasteItem, Long> {
    List<WasteItem> findByCategory(String category);

    @Query("SELECT w FROM WasteItem w WHERE LOWER(w.name) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<WasteItem> searchByName(@Param("query") String query);

    List<WasteItem> findByRecyclableTrue();
}
