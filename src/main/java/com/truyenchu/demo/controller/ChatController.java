package com.truyenchu.demo.controller;

import com.truyenchu.demo.entity.ChatMessage;
import com.truyenchu.demo.entity.User;
import com.truyenchu.demo.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatController {
    private final ChatService chatService;

    @GetMapping("/conversation/{userId}")
    public ResponseEntity<Page<ChatMessage>> getConversation(
            @AuthenticationPrincipal User currentUser,
            @PathVariable Long userId,
            Pageable pageable) {
        User otherUser = new User();
        otherUser.setId(userId);
        return ResponseEntity.ok(chatService.findConversation(currentUser, otherUser, pageable));
    }

    @GetMapping("/unread")
    public ResponseEntity<Page<ChatMessage>> getUnreadMessages(
            @AuthenticationPrincipal User user,
            Pageable pageable) {
        return ResponseEntity.ok(chatService.findUnreadMessages(user, pageable));
    }

    @GetMapping("/unread/count")
    public ResponseEntity<Long> getUnreadMessageCount(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(chatService.countUnreadMessages(user));
    }

    @GetMapping("/partners")
    public ResponseEntity<Page<User>> getChatPartners(
            @AuthenticationPrincipal User user,
            Pageable pageable) {
        return ResponseEntity.ok(chatService.findChatPartners(user, pageable));
    }

    @PostMapping("/message/{messageId}/read")
    public ResponseEntity<Void> markMessageAsRead(
            @AuthenticationPrincipal User user,
            @PathVariable Long messageId) {
        chatService.markMessageAsRead(messageId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/conversation/{userId}/read")
    public ResponseEntity<Void> markConversationAsRead(
            @AuthenticationPrincipal User currentUser,
            @PathVariable Long userId) {
        User otherUser = new User();
        otherUser.setId(userId);
        chatService.markConversationAsRead(currentUser, otherUser);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/message/{messageId}")
    public ResponseEntity<Void> deleteMessage(
            @AuthenticationPrincipal User user,
            @PathVariable Long messageId) {
        chatService.deleteMessage(messageId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/conversation/{userId}")
    public ResponseEntity<Void> deleteConversation(
            @AuthenticationPrincipal User currentUser,
            @PathVariable Long userId) {
        User otherUser = new User();
        otherUser.setId(userId);
        chatService.deleteConversation(currentUser, otherUser);
        return ResponseEntity.ok().build();
    }
} 