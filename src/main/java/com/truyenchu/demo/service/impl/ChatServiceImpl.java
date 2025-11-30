package com.truyenchu.demo.service.impl;

import com.truyenchu.demo.entity.ChatMessage;
import com.truyenchu.demo.entity.User;
import com.truyenchu.demo.repository.ChatMessageRepository;
import com.truyenchu.demo.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class ChatServiceImpl implements ChatService {
    private final ChatMessageRepository chatMessageRepository;

    @Override
    public ChatMessage sendMessage(User sender, User receiver, String content, ChatMessage.MessageType type) {
        ChatMessage message = new ChatMessage();
        message.setSender(sender);
        message.setReceiver(receiver);
        message.setContent(content);
        message.setType(type);
        message.setRead(false);
        return chatMessageRepository.save(message);
    }

    @Override
    public void markMessageAsRead(Long messageId) {
        ChatMessage message = findById(messageId);
        message.setRead(true);
        chatMessageRepository.save(message);
    }

    @Override
    public void markConversationAsRead(User user1, User user2) {
        chatMessageRepository.findConversation(user1, user2, Pageable.unpaged())
                .forEach(message -> {
                    if (message.getReceiver().equals(user1)) {
                        message.setRead(true);
                        chatMessageRepository.save(message);
                    }
                });
    }

    @Override
    public void deleteMessage(Long messageId) {
        chatMessageRepository.deleteById(messageId);
    }

    @Override
    public void deleteConversation(User user1, User user2) {
        chatMessageRepository.findConversation(user1, user2, Pageable.unpaged())
                .forEach(message -> chatMessageRepository.delete(message));
    }

    @Override
    @Transactional(readOnly = true)
    public ChatMessage findById(Long id) {
        return chatMessageRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Message not found"));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ChatMessage> findConversation(User user1, User user2, Pageable pageable) {
        return chatMessageRepository.findConversation(user1, user2, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ChatMessage> findUnreadMessages(User user, Pageable pageable) {
        return chatMessageRepository.findUnreadMessages(user, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public long countUnreadMessages(User user) {
        return chatMessageRepository.countUnreadMessages(user);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<User> findChatPartners(User user, Pageable pageable) {
        return chatMessageRepository.findChatPartners(user, pageable);
    }

    @Override
    public void sendSystemMessage(User receiver, String content) {
        ChatMessage message = new ChatMessage();
        message.setSender(null); // System message
        message.setReceiver(receiver);
        message.setContent(content);
        message.setType(ChatMessage.MessageType.TEXT);
        message.setRead(false);
        chatMessageRepository.save(message);
    }
} 