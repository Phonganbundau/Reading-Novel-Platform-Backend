package com.truyenchu.demo.controller;

import com.truyenchu.demo.entity.Gift;
import com.truyenchu.demo.entity.GiftTransaction;
import com.truyenchu.demo.entity.Story;
import com.truyenchu.demo.entity.User;
import com.truyenchu.demo.repository.GiftRepository;
import com.truyenchu.demo.repository.GiftTransactionRepository;
import com.truyenchu.demo.repository.StoryRepository;
import com.truyenchu.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/gifts")
@RequiredArgsConstructor
public class GiftController {
    private final GiftRepository giftRepository;
    private final GiftTransactionRepository giftTransactionRepository;
    private final StoryRepository storyRepository;
    private final UserRepository userRepository;

    @PostMapping("/give")
    public ResponseEntity<?> giveGift(@RequestBody Map<String, Object> request, Authentication authentication) {
        Long storyId = Long.valueOf(request.get("storyId").toString());
        Long giftId = Long.valueOf(request.get("giftId").toString());
        Long receiverId = Long.valueOf(request.get("receiverId").toString());
        int quantity = Integer.parseInt(request.getOrDefault("quantity", 1).toString());

        User sender = userRepository.findByUsername(authentication.getName())
            .orElseThrow(() -> new RuntimeException("Sender not found"));
        User receiver = userRepository.findById(receiverId)
            .orElseThrow(() -> new RuntimeException("Receiver not found"));
        Story story = storyRepository.findById(storyId)
            .orElseThrow(() -> new RuntimeException("Story not found"));
        Gift gift = giftRepository.findById(giftId)
            .orElseThrow(() -> new RuntimeException("Gift not found"));

        BigDecimal totalCost = gift.getCoinCosts().multiply(BigDecimal.valueOf(quantity));
        if (sender.getCoinBalance().compareTo(totalCost) < 0) {
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Số dư không đủ để tặng quà");
            response.put("price", totalCost);
            response.put("balance", sender.getCoinBalance());
            return ResponseEntity.badRequest().body(response);
        }

        // Trừ tiền
        sender.setCoinBalance(sender.getCoinBalance().subtract(totalCost));
        receiver.setCoinEarning(receiver.getCoinEarning().add(totalCost));
        userRepository.save(sender);
        userRepository.save(receiver);

        // Ghi nhận transaction
        for (int i = 0; i < quantity; i++) {
            GiftTransaction transaction = new GiftTransaction();
            transaction.setSender(sender);
            transaction.setReceiver(receiver);
            transaction.setStory(story);
            transaction.setGift(gift);
            giftTransactionRepository.save(transaction);
        }

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Tặng quà thành công");
        response.put("giftName", gift.getName());
        response.put("quantity", quantity);
        return ResponseEntity.ok(response);
    }
} 