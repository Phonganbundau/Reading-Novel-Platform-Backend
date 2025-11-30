package com.truyenchu.demo.controller;

import com.truyenchu.demo.dto.PlazaDTO;
import com.truyenchu.demo.dto.PlazaLikeResponse;
import com.truyenchu.demo.dto.PlazaRequest;
import com.truyenchu.demo.entity.Plaza;
import com.truyenchu.demo.entity.Story;
import com.truyenchu.demo.entity.User;
import com.truyenchu.demo.repository.StoryRepository;
import com.truyenchu.demo.repository.UserRepository;
import com.truyenchu.demo.service.PlazaService;
import com.truyenchu.demo.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/plazas")
@RequiredArgsConstructor
public class PlazaController {
    private final PlazaService plazaService;
    private final UserRepository userRepository;
    private final StoryRepository storyRepository;
    private final NotificationService notificationService;

    @GetMapping("/story/{storyId}")
    public ResponseEntity<Page<PlazaDTO>> getPlazasByStory(@PathVariable Long storyId, Pageable pageable) {
        Story story = storyRepository.findById(storyId)
            .orElseThrow(() -> new RuntimeException("Story not found"));
        
        // Get current user
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User currentUser = userRepository.findByUsername(username)
            .orElse(null); // Allow anonymous access
        
        // Lấy chỉ những plaza gốc (không có parent) với phân trang
        Page<Plaza> rootPlazasPage = plazaService.findRootPlazasByStory(story, pageable);
        
        Page<PlazaDTO> plazasPage = rootPlazasPage.map(rootPlaza -> {
            PlazaDTO dto = PlazaDTO.fromEntity(rootPlaza, currentUser);
            
            // Lấy tối đa 6 replies đầu tiên của root plaza này
            List<Plaza> allReplies = plazaService.findByRootPlaza(rootPlaza);
            List<PlazaDTO> replyDtos = allReplies.stream()
                .sorted(java.util.Comparator.comparing(Plaza::getCreatedAt))
                .limit(6)
                .map(reply -> {
                    PlazaDTO replyDto = PlazaDTO.fromEntity(reply, currentUser);
                    if (currentUser != null) {
                        replyDto.setLiked(plazaService.isLikedByUser(reply.getId(), currentUser.getId()));
                    }
                    return replyDto;
                })
                .collect(Collectors.toList());
            
            dto.setReplies(replyDtos);
            if (currentUser != null) {
                dto.setLiked(plazaService.isLikedByUser(rootPlaza.getId(), currentUser.getId()));
            }
            
            return dto;
        });
        
        return ResponseEntity.ok(plazasPage);
    }

    @PostMapping("/story/{storyId}")
    public ResponseEntity<PlazaDTO> addPlaza(
            @PathVariable Long storyId,
            @RequestBody PlazaRequest request) {
        
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        Story story = storyRepository.findById(storyId)
            .orElseThrow(() -> new RuntimeException("Story not found"));

        Plaza parentPlaza = null;
        if (request.getParentPlazaId() != null) {
            parentPlaza = plazaService.findById(request.getParentPlazaId());
        }

        Plaza plaza = plazaService.createPlaza(request.getContent(), story, user, parentPlaza);
        return ResponseEntity.ok(PlazaDTO.fromEntity(plaza));
    }

    @PostMapping("/{plazaId}/reply")
    public ResponseEntity<PlazaDTO> replyToPlaza(
            @PathVariable Long plazaId,
            @RequestBody PlazaRequest request) {
        
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        Plaza parentPlaza = plazaService.findById(plazaId);
        if (parentPlaza == null) {
            throw new RuntimeException("Parent plaza not found");
        }
        
        Story story = parentPlaza.getStory();
        if (story == null) {
            throw new RuntimeException("Story not found for the parent plaza");
        }

        Plaza reply = plazaService.createPlaza(request.getContent(), story, user, parentPlaza);

        // Tạo notification cho user của parentPlaza nếu không phải reply chính mình
        if (!user.getId().equals(parentPlaza.getUser().getId())) {
            notificationService.createPlazaReplyNotification(
                parentPlaza.getUser(),
                user,
                parentPlaza,
                reply,
                parentPlaza.getRootPlaza() != null ? parentPlaza.getRootPlaza().getId() : parentPlaza.getId()
            );
        }

        return ResponseEntity.ok(PlazaDTO.fromEntity(reply));
    }

    @PutMapping("/{plazaId}")
    public ResponseEntity<PlazaDTO> updatePlaza(
            @PathVariable Long plazaId,
            @RequestBody PlazaRequest request) {
        
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        
        Plaza plaza = plazaService.findById(plazaId);
        
        // Kiểm tra xem plaza có phải của user hiện tại không
        if (!plaza.getUser().getUsername().equals(username)) {
            throw new RuntimeException("You can only update your own plazas");
        }
        
        Plaza updatedPlaza = plazaService.updatePlaza(plazaId, request.getContent());
        return ResponseEntity.ok(PlazaDTO.fromEntity(updatedPlaza));
    }

    @DeleteMapping("/{plazaId}")
    public ResponseEntity<Void> deletePlaza(@PathVariable Long plazaId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        
        Plaza plaza = plazaService.findById(plazaId);
        
        // Kiểm tra xem plaza có phải của user hiện tại không
        if (!plaza.getUser().getUsername().equals(username)) {
            throw new RuntimeException("You can only delete your own plazas");
        }
        
        plazaService.deletePlaza(plazaId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{plazaId}/replies")
    public ResponseEntity<List<PlazaDTO>> getPlazaReplies(@PathVariable Long plazaId) {
        Plaza parentPlaza = plazaService.findById(plazaId);
        
        // Get current user for like status
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User currentUser = userRepository.findByUsername(username)
            .orElse(null); // Allow anonymous access
        
        List<PlazaDTO> replies = parentPlaza.getReplies().stream()
            .map(reply -> {
                PlazaDTO dto = PlazaDTO.fromEntity(reply, currentUser);
                
                // Set isLiked status if current user is authenticated
                if (currentUser != null) {
                    dto.setLiked(plazaService.isLikedByUser(reply.getId(), currentUser.getId()));
                }
                
                return dto;
            })
            .collect(Collectors.toList());
        
        return ResponseEntity.ok(replies);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<Page<PlazaDTO>> getPlazasByUser(@PathVariable Long userId, Pageable pageable) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        // Get current user for like status
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User currentUser = userRepository.findByUsername(username)
            .orElse(null); // Allow anonymous access
        
        Page<Plaza> plazasPage = plazaService.findByUser(user, pageable);
        Page<PlazaDTO> plazasDTOPage = plazasPage.map(plaza -> {
            PlazaDTO dto = PlazaDTO.fromEntity(plaza, currentUser);
            
            // Lấy tối đa 6 replies đầu tiên
            List<Plaza> allReplies = plaza.getReplies();
            List<PlazaDTO> replyDtos = allReplies.stream()
                .sorted(java.util.Comparator.comparing(Plaza::getCreatedAt))
                .limit(6)
                .map(reply -> {
                    PlazaDTO replyDto = PlazaDTO.fromEntity(reply, currentUser);
                    if (currentUser != null) {
                        replyDto.setLiked(plazaService.isLikedByUser(reply.getId(), currentUser.getId()));
                    }
                    return replyDto;
                })
                .collect(Collectors.toList());
            dto.setReplies(replyDtos);
            if (currentUser != null) {
                dto.setLiked(plazaService.isLikedByUser(plaza.getId(), currentUser.getId()));
            }
            return dto;
        });
        
        return ResponseEntity.ok(plazasDTOPage);
    }

    @PostMapping("/{plazaId}/like")
    public ResponseEntity<PlazaLikeResponse> toggleLike(@PathVariable Long plazaId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        PlazaLikeResponse response = plazaService.toggleLike(plazaId, user.getId());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/story/{storyId}/count")
    public ResponseEntity<Long> getPlazaCountByStory(@PathVariable Long storyId) {
        Story story = storyRepository.findById(storyId)
            .orElseThrow(() -> new RuntimeException("Story not found"));
        
        long count = plazaService.countByStory(story);
        return ResponseEntity.ok(count);
    }

    @GetMapping("/all")
    public ResponseEntity<Page<PlazaDTO>> getAllPlazas(Pageable pageable) {
        // Get current user
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User currentUser = userRepository.findByUsername(username)
            .orElse(null); // Allow anonymous access
        
        // Chỉ lấy các plaza gốc (parentPlaza IS NULL)
        Page<Plaza> plazasPage = plazaService.findRootPlazas(pageable);
        Page<PlazaDTO> plazasDTOPage = plazasPage.map(plaza -> {
            PlazaDTO dto = PlazaDTO.fromEntity(plaza, currentUser);
            // Lấy tối đa 6 replies đầu tiên
            List<Plaza> allReplies = plaza.getReplies();
            List<PlazaDTO> replyDtos = allReplies.stream()
                .sorted(java.util.Comparator.comparing(Plaza::getCreatedAt))
                .limit(6)
                .map(reply -> {
                    PlazaDTO replyDto = PlazaDTO.fromEntity(reply, currentUser);
                    if (currentUser != null) {
                        replyDto.setLiked(plazaService.isLikedByUser(reply.getId(), currentUser.getId()));
                    }
                    return replyDto;
                })
                .collect(Collectors.toList());
            dto.setReplies(replyDtos);
            if (currentUser != null) {
                dto.setLiked(plazaService.isLikedByUser(plaza.getId(), currentUser.getId()));
            }
            return dto;
        });
        return ResponseEntity.ok(plazasDTOPage);
    }
} 