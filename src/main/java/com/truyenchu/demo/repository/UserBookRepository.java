package com.truyenchu.demo.repository;

import com.truyenchu.demo.entity.User;
import com.truyenchu.demo.entity.UserBook;
import com.truyenchu.demo.entity.UserBookStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserBookRepository extends JpaRepository<UserBook, Long> {
    List<UserBook> findByUserAndStatus(User user, UserBookStatus status);
    int countByUserAndStatus(User user, UserBookStatus status);
} 