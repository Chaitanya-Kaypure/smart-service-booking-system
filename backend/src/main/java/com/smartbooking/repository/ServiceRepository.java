package com.smartbooking.repository;

import com.smartbooking.entity.ServiceEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ServiceRepository extends JpaRepository<ServiceEntity, Long> {

    Page<ServiceEntity> findByActiveTrue(Pageable pageable);

    @Query("""
            SELECT DISTINCT s.category FROM ServiceEntity s
            WHERE s.active = TRUE
            ORDER BY s.category
            """)
    List<String> findDistinctActiveCategories();

    @Query("""
            SELECT s FROM ServiceEntity s
            WHERE s.active = TRUE
              AND (:category IS NULL OR :category = '' OR s.category = :category)
              AND (:search IS NULL OR :search = ''
                   OR LOWER(s.name) LIKE LOWER(CONCAT('%', :search, '%'))
                   OR LOWER(s.description) LIKE LOWER(CONCAT('%', :search, '%')))
            """)
    Page<ServiceEntity> searchActive(
            @Param("search") String search,
            @Param("category") String category,
            Pageable pageable);
}
