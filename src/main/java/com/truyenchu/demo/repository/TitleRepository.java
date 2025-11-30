package com.truyenchu.demo.repository;

import com.truyenchu.demo.entity.Title;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TitleRepository extends JpaRepository<Title, Long> {
    Title findByName(String name);
} 