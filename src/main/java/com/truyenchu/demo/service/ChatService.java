package com.truyenchu.demo.service;

import com.truyenchu.demo.entity.ChatMessage;
import com.truyenchu.demo.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ChatService {
    ChatMessage sendMessage(User sender, User receiver, String content, ChatMessage.MessageType type);
    void markMessageAsRead(Long messageId);
    void markConversationAsRead(User user1, User user2);
    void deleteMessage(Long messageId);
    void deleteConversation(User user1, User user2);
    ChatMessage findById(Long id);
    Page<ChatMessage> findConversation(User user1, User user2, Pageable pageable);
    Page<ChatMessage> findUnreadMessages(User user, Pageable pageable);
    long countUnreadMessages(User user);
    Page<User> findChatPartners(User user, Pageable pageable);
    void sendSystemMessage(User receiver, String content);
} 