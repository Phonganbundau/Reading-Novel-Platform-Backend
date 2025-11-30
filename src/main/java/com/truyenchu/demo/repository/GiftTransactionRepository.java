package com.truyenchu.demo.repository;

import com.truyenchu.demo.entity.GiftTransaction;
import com.truyenchu.demo.entity.Story;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Repository
public interface GiftTransactionRepository extends JpaRepository<GiftTransaction, Long> {
    // Thống kê theo thời gian
    @Query("SELECT COALESCE(SUM(gt.gift.coinCosts), 0) FROM GiftTransaction gt WHERE gt.story = :story")
    BigDecimal sumGiftAmountByStory(@Param("story") Story story);

    @Query("SELECT COALESCE(SUM(gt.gift.coinCosts), 0) FROM GiftTransaction gt WHERE gt.story = :story AND gt.createdAt >= :dateTime")
    BigDecimal sumGiftAmountByStoryAndCreatedAtAfter(@Param("story") Story story, @Param("dateTime") LocalDateTime dateTime);
} 