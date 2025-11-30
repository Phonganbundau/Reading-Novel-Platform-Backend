package com.truyenchu.demo.repository;

import com.truyenchu.demo.entity.EditorPick;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface EditorPickRepository extends JpaRepository<EditorPick, Long> {
    @Query("SELECT e FROM EditorPick e WHERE (:genre IS NULL OR LOWER(e.story.genre.name) = LOWER(:genre))")
    Page<EditorPick> findByGenreName(@Param("genre") String genre, Pageable pageable);
} 