package com.truyenchu.demo.dto;

import com.truyenchu.demo.entity.User;
import lombok.Data;

@Data
public class StoryFanDTO {
    private Long userId;
    private String username;
    private String avatar;
    private String title;
    private Long votePoints; // Điểm từ phiếu đề cử (1 phiếu = 1000 điểm)
    private Long giftPoints; // Điểm từ quà tặng (10 coin = 1000 điểm)
    private Long totalPoints; // Tổng điểm hâm mộ
    private Long voteCount; // Số phiếu đề cử
    private Long giftCoins; // Tổng số coin tặng quà

    public static StoryFanDTO fromEntity(User user, Long voteCount, Long giftCoins) {
        StoryFanDTO dto = new StoryFanDTO();
        dto.setUserId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setAvatar(user.getAvatar());
        dto.setTitle(user.getTitle() != null ? user.getTitle().getName() : null);
        dto.setVoteCount(voteCount != null ? voteCount : 0L);
        dto.setGiftCoins(giftCoins != null ? giftCoins : 0L);
        
        // Tính điểm hâm mộ
        dto.setVotePoints(dto.getVoteCount() * 1000); // 1 phiếu = 1000 điểm
        dto.setGiftPoints(dto.getGiftCoins() * 100); // 10 coin = 1000 điểm, nên 1 coin = 100 điểm
        dto.setTotalPoints(dto.getVotePoints() + dto.getGiftPoints());
        
        return dto;
    }
} 