package com.edi.backend.repository;

import com.edi.backend.domain.Menu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface MenuRepository extends JpaRepository<Menu, String> {

    @Query("""
            SELECT m
            FROM Menu m
            LEFT JOIN FETCH m.items i
            LEFT JOIN FETCH i.application
            LEFT JOIN FETCH i.subMenu
            WHERE m.id = :id
            """)
    Optional<Menu> findByIdWithItems(@Param("id") String id);
}
