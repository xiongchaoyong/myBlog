package com.x.pojo.entity;

import com.x.common.enmu.LikeType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LikeMessage {
    private Long commentId;
    private Long userId;
    private LikeType likeType;
    private LikeAction action; // 点赞或取消点赞
    
    public enum LikeAction {
        LIKE, UNLIKE
    }
}