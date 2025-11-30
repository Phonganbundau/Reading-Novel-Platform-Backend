package com.truyenchu.demo.controller;

import com.truyenchu.demo.entity.ChatMessage;
import com.truyenchu.demo.entity.User;
import com.truyenchu.demo.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class ChatWebSocketController {
    private final ChatService chatService;
    private final SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/chat.send")
    public void sendMessage(@Payload ChatMessage chatMessage, SimpMessageHeaderAccessor headerAccessor) {
        Authentication auth = (Authentication) headerAccessor.getUser();
        User sender = (User) auth.getPrincipal();
        
        // Save the message
        ChatMessage savedMessage = chatService.sendMessage(
            sender,
            chatMessage.getReceiver(),
            chatMessage.getContent(),
            chatMessage.getType()
        );

        // Send to receiver
        messagingTemplate.convertAndSendToUser(
            chatMessage.getReceiver().getUsername(),
            "/queue/messages",
            savedMessage
        );

        // Send confirmation to sender
        messagingTemplate.convertAndSendToUser(
            sender.getUsername(),
            "/queue/messages",
            savedMessage
        );
    }

    @MessageMapping("/chat.markAsRead")
    public void markMessageAsRead(@Payload Long messageId, SimpMessageHeaderAccessor headerAccessor) {
        Authentication auth = (Authentication) headerAccessor.getUser();
        User user = (User) auth.getPrincipal();
        
        ChatMessage message = chatService.findById(messageId);
        if (message.getReceiver().equals(user)) {
            chatService.markMessageAsRead(messageId);
            
            // Notify sender that message was read
            messagingTemplate.convertAndSendToUser(
                message.getSender().getUsername(),
                "/queue/messages/read",
                messageId
            );
        }
    }

    @MessageMapping("/chat.delete")
    public void deleteMessage(@Payload Long messageId, SimpMessageHeaderAccessor headerAccessor) {
        Authentication auth = (Authentication) headerAccessor.getUser();
        User user = (User) auth.getPrincipal();
        
        ChatMessage message = chatService.findById(messageId);
        if (message.getSender().equals(user) || message.getReceiver().equals(user)) {
            chatService.deleteMessage(messageId);
            
            // Notify both users about message deletion
            messagingTemplate.convertAndSendToUser(
                message.getSender().getUsername(),
                "/queue/messages/deleted",
                messageId
            );
            messagingTemplate.convertAndSendToUser(
                message.getReceiver().getUsername(),
                "/queue/messages/deleted",
                messageId
            );
        }
    }
} 